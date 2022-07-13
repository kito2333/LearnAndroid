package com.example.helloworld.ui

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.GestureDetector
import android.widget.TextView
import android.widget.Toast
import com.example.helloworld.R
import com.google.android.filament.Fence
import com.google.android.filament.IndirectLight
import com.google.android.filament.Skybox
import com.google.android.filament.View
import com.google.android.filament.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.zip.ZipInputStream

class GLTFActivity : AppCompatActivity() {
    companion object {
        const val TAG = "gltf-viewer"
    }

    private lateinit var surfaceView: SurfaceView
    private lateinit var choreographer: Choreographer
    private val frameScheduler = FrameCallback()
    private lateinit var modelViewer: ModelViewer
    private lateinit var titleBarHint: TextView
    private lateinit var sensorParamText: TextView
    private val doubleTapListener = DoubleTapListener()
    private lateinit var doubleTapDetector: GestureDetector
    private var remoteServer: RemoteServer? = null
    private var statusToast: Toast? = null
    private var statusText: String? = null
    private var latestDownload: String? = null
    private val automation = AutomationEngine()
    private var loadStartTime = 0L
    private var loadStartFence: Fence? = null
    private val viewerContent = AutomationEngine.ViewerContent()

    private var sensor: Sensor? = null
    private var sensorAvailable: Boolean = false
    private var sensorFirstGet: Boolean = false
    private var sensorRotationMatrix: FloatArray = FloatArray(0)
    private lateinit var sensorManager: SensorManager
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.values != null) {
                val rotationMatrix = FloatArray(16)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                if (!sensorFirstGet) {
                    Log.d(TAG, "fckAll init succeed $rotationMatrix")
                    sensorFirstGet = true
                    sensorRotationMatrix = FloatArray(16) {
                        rotationMatrix[it]
                    }
                }

                val orientations = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientations)
                for (i in 0 until 3) {
                    orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
                }
                sensorParamText.text = resources.getString(
                    R.string.sensor_param,
                    orientations[1],
                    orientations[2],
                    orientations[0]
                )

                val finalMatrix = FloatArray(16)
                modelViewer.asset?.let { asset ->
                    val tm = modelViewer.engine.transformManager
                    if (Matrix.invertM(rotationMatrix, 0, rotationMatrix, 0)) {
                        Matrix.multiplyMM(
                            finalMatrix,
                            0,
                            sensorRotationMatrix,
                            0,
                            rotationMatrix,
                            0
                        )
                        finalMatrix[14] = -400.0f
                        tm.setTransform(tm.getInstance(asset.root), finalMatrix)
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gltf)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initSensor()

        titleBarHint = findViewById(R.id.user_hint)
        surfaceView = findViewById(R.id.main_sv)
        choreographer = Choreographer.getInstance()

        doubleTapDetector = GestureDetector(applicationContext, doubleTapListener)

        modelViewer = ModelViewer(surfaceView)
        viewerContent.view = modelViewer.view
        viewerContent.sunlight = modelViewer.light
        viewerContent.lightManager = modelViewer.engine.lightManager
        viewerContent.scene = modelViewer.scene
        viewerContent.renderer = modelViewer.renderer

        surfaceView.setOnTouchListener { _, event ->
            modelViewer.onTouchEvent(event)
            doubleTapDetector.onTouchEvent(event)
            true
        }

        createDefaultRenderables()
        createIndirectLight()

        setStatusText("To load a new model, go to the above URL on your host machine.")

        val view = modelViewer.view

        /*
         * Note: The settings below are overriden when connecting to the remote UI.
         */

        // on mobile, better use lower quality color buffer
        view.renderQuality = view.renderQuality.apply {
            hdrColorBuffer = View.QualityLevel.MEDIUM
        }

        // dynamic resolution often helps a lot
        view.dynamicResolutionOptions = view.dynamicResolutionOptions.apply {
            enabled = true
            quality = View.QualityLevel.MEDIUM
        }

        // MSAA is needed with dynamic resolution MEDIUM
        view.multiSampleAntiAliasingOptions = view.multiSampleAntiAliasingOptions.apply {
            enabled = true
        }

        // FXAA is pretty cheap and helps a lot
        view.antiAliasing = View.AntiAliasing.FXAA;

        // ambient occlusion is the cheapest effect that adds a lot of quality
        view.ambientOcclusionOptions = view.ambientOcclusionOptions.apply {
            enabled = true
        }

        // bloom is pretty expensive but adds a fair amount of realism
        view.bloomOptions = view.bloomOptions.apply {
            enabled = true
        }

        remoteServer = RemoteServer(8082)
    }

    private fun createDefaultRenderables() {
        val buffer = assets.open("models/scene.gltf").use { input ->
            val bytes = ByteArray(input.available())
            input.read(bytes)
            ByteBuffer.wrap(bytes)
        }

        modelViewer.loadModelGltfAsync(buffer) { uri -> readCompressedAsset("models/$uri") }
        updateRootTransform()
    }

    private fun createIndirectLight() {
        val engine = modelViewer.engine
        val scene = modelViewer.scene
        val ibl = "default_env"
        readCompressedAsset("envs/$ibl/${ibl}_ibl.ktx").let {
            scene.indirectLight = KTX1Loader.createIndirectLight(engine, it)
            scene.indirectLight!!.intensity = 30_000.0f
            viewerContent.indirectLight = modelViewer.scene.indirectLight
        }
        readCompressedAsset("envs/$ibl/${ibl}_skybox.ktx").let {
            scene.skybox = KTX1Loader.createSkybox(engine, it)
        }
    }

    private fun readCompressedAsset(assetName: String): ByteBuffer {
        val input = assets.open(assetName)
        val bytes = ByteArray(input.available())
        input.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    private fun clearStatusText() {
        statusToast?.let {
            it.cancel()
            statusText = null
        }
    }

    private fun setStatusText(text: String) {
        runOnUiThread {
            if (statusToast == null || statusText != text) {
                statusText = text
                statusToast = Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT)
                statusToast!!.show()

            }
        }
    }

    private suspend fun loadGlb(message: RemoteServer.ReceivedMessage) {
        withContext(Dispatchers.Main) {
            modelViewer.destroyModel()
            modelViewer.loadModelGlb(message.buffer)
            updateRootTransform()
            loadStartTime = System.nanoTime()
            loadStartFence = modelViewer.engine.createFence()
        }
    }

    private suspend fun loadHdr(message: RemoteServer.ReceivedMessage) {
        withContext(Dispatchers.Main) {
            val engine = modelViewer.engine
            val equirect = HDRLoader.createTexture(engine, message.buffer)
            if (equirect == null) {
                setStatusText("Could not decode HDR file.")
            } else {
                setStatusText("Successfully decoded HDR file.")

                val context = IBLPrefilterContext(engine)
                val equirectToCubemap = IBLPrefilterContext.EquirectangularToCubemap(context)
                val skyboxTexture = equirectToCubemap.run(equirect)!!
                engine.destroyTexture(equirect)

                val specularFilter = IBLPrefilterContext.SpecularFilter(context)
                val reflections = specularFilter.run(skyboxTexture)

                val ibl = IndirectLight.Builder()
                    .reflections(reflections)
                    .intensity(30000.0f)
                    .build(engine)

                val sky = Skybox.Builder().environment(skyboxTexture).build(engine)

                specularFilter.destroy();
                equirectToCubemap.destroy();
                context.destroy();

                modelViewer.scene.skybox = sky
                modelViewer.scene.indirectLight = ibl
            }
        }
    }

    private suspend fun loadZip(message: RemoteServer.ReceivedMessage) {
        // To alleviate memory pressure, remove the old model before deflating the zip.
        withContext(Dispatchers.Main) {
            modelViewer.destroyModel()
        }

        // Large zip files should first be written to a file to prevent OOM.
        // It is also crucial that we null out the message "buffer" field.
        val (zipStream, zipFile) = withContext(Dispatchers.IO) {
            val file = File.createTempFile("incoming", "zip", cacheDir)
            val raf = RandomAccessFile(file, "rw")
            raf.channel.write(message.buffer);
            message.buffer = null
            raf.seek(0)
            Pair(FileInputStream(file), file)
        }

        // Deflate each resource using the IO dispatcher, one by one.
        var gltfPath: String? = null
        var outOfMemory: String? = null
        val pathToBufferMapping = withContext(Dispatchers.IO) {
            val deflater = ZipInputStream(zipStream)
            val mapping = HashMap<String, Buffer>()
            while (true) {
                val entry = deflater.nextEntry ?: break
                if (entry.isDirectory) continue

                // This isn't strictly required, but as an optimization
                // we ignore common junk that often pollutes ZIP files.
                if (entry.name.startsWith("__MACOSX")) continue
                if (entry.name.startsWith(".DS_Store")) continue

                val uri = entry.name
                val byteArray: ByteArray? = try {
                    deflater.readBytes()
                } catch (e: OutOfMemoryError) {
                    outOfMemory = uri
                    break
                }
                Log.i(TAG, "Deflated ${byteArray!!.size} bytes from $uri")
                val buffer = ByteBuffer.wrap(byteArray)
                mapping[uri] = buffer
                if (uri.endsWith(".gltf") || uri.endsWith(".glb")) {
                    gltfPath = uri
                }
            }
            mapping
        }

        zipFile.delete()

        if (gltfPath == null) {
            setStatusText("Could not find .gltf or .glb in the zip.")
            return
        }

        if (outOfMemory != null) {
            setStatusText("Out of memory while deflating $outOfMemory")
            return
        }

        val gltfBuffer = pathToBufferMapping[gltfPath]!!

        // The gltf is often not at the root level (e.g. if a folder is zipped) so
        // we need to extract its path in order to resolve the embedded uri strings.
        var gltfPrefix = gltfPath!!.substringBeforeLast('/', "")
        if (gltfPrefix.isNotEmpty()) {
            gltfPrefix += "/"
        }

        withContext(Dispatchers.Main) {
            if (gltfPath!!.endsWith(".glb")) {
                modelViewer.loadModelGlb(gltfBuffer)
            } else {
                modelViewer.loadModelGltf(gltfBuffer) { uri ->
                    val path = gltfPrefix + uri
                    if (!pathToBufferMapping.contains(path)) {
                        Log.e(TAG, "Could not find $path in the zip.")
                        setStatusText("Zip is missing $path")
                    }
                    pathToBufferMapping[path]
                }
            }
            updateRootTransform()
            loadStartTime = System.nanoTime()
            loadStartFence = modelViewer.engine.createFence()
        }
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(frameScheduler)
        registerSensorListener(true)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(frameScheduler)
        registerSensorListener(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(frameScheduler)
        remoteServer?.close()
    }

    fun loadModelData(message: RemoteServer.ReceivedMessage) {
        Log.i(TAG, "Downloaded model ${message.label} (${message.buffer.capacity()} bytes)")
        clearStatusText()
        titleBarHint.text = message.label
        CoroutineScope(Dispatchers.IO).launch {
            when {
                message.label.endsWith(".zip") -> {
                    loadZip(message)
                }
                message.label.endsWith(".hdr") -> {
                    loadHdr(message)
                }
                else -> {
                    loadGlb(message)
                }
            }
        }
    }

    fun loadSettings(message: RemoteServer.ReceivedMessage) {
        val json = StandardCharsets.UTF_8.decode(message.buffer).toString()
        viewerContent.assetLights = modelViewer.asset?.lightEntities
        automation.applySettings(json, viewerContent)
        modelViewer.view.colorGrading = automation.getColorGrading(modelViewer.engine)
        modelViewer.cameraFocalLength = automation.viewerOptions.cameraFocalLength
        updateRootTransform()
    }

    private fun updateRootTransform() {
        if (automation.viewerOptions.autoScaleEnabled) {
            modelViewer.transformToUnitCube()
        } else {
            modelViewer.clearRootTransform()
        }
    }

    inner class FrameCallback : Choreographer.FrameCallback {
        private val startTime = System.nanoTime()
        override fun doFrame(frameTimeNanos: Long) {
            choreographer.postFrameCallback(this)

            loadStartFence?.let {
                if (it.wait(Fence.Mode.FLUSH, 0) == Fence.FenceStatus.CONDITION_SATISFIED) {
                    val end = System.nanoTime()
                    val total = (end - loadStartTime) / 1_000_000
                    Log.i(TAG, "The Filament backend took $total ms to load the model geometry.")
                    modelViewer.engine.destroyFence(it)
                    loadStartFence = null
                }
            }

            modelViewer.animator?.apply {
                if (animationCount > 0) {
                    val elapsedTimeSeconds = (frameTimeNanos - startTime).toDouble() / 1_000_000_000
                    applyAnimation(0, elapsedTimeSeconds.toFloat())
                }
                updateBoneMatrices()
            }

            modelViewer.render(frameTimeNanos)

            // Check if a new download is in progress. If so, let the user know with toast.
            val currentDownload = remoteServer?.peekIncomingLabel()
            if (RemoteServer.isBinary(currentDownload) && currentDownload != latestDownload) {
                latestDownload = currentDownload
                Log.i(TAG, "Downloading $currentDownload")
                setStatusText("Downloading $currentDownload")
            }

            // Check if a new message has been fully received from the client.
            val message = remoteServer?.acquireReceivedMessage()
            if (message != null) {
                if (message.label == latestDownload) {
                    latestDownload = null
                }
                if (RemoteServer.isJson(message.label)) {
                    loadSettings(message)
                } else {
                    loadModelData(message)
                }
            }
        }
    }

    // Just for testing purposes, this releases the current model and reloads the default model.
    inner class DoubleTapListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent?): Boolean {
            modelViewer.destroyModel()
            createDefaultRenderables()
            return super.onDoubleTap(e)
        }
    }

    private fun initSensor() {
        sensorParamText = findViewById(R.id.sensor_param)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: return
        sensorAvailable = true
        Log.d(TAG, "sensor init succeed")
    }

    private fun registerSensorListener(register: Boolean) {
        if (sensorAvailable) {
            if (register) {
                sensorManager.registerListener(
                    sensorListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            } else {
                sensorManager.unregisterListener(sensorListener, sensor)
            }
        }
    }
}