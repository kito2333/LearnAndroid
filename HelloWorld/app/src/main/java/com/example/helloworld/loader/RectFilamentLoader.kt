package com.example.helloworld.loader

import android.animation.ValueAnimator
import android.opengl.Matrix
import android.view.Surface
import android.view.animation.LinearInterpolator
import com.google.android.filament.*
import com.google.android.filament.android.UiHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RectFilamentLoader : FilamentLoader() {

    override fun initView() {
        scene.skybox = Skybox.Builder().color(0.035f, 0.035f, 0.035f, 1.0f).build(engine)
        super.initView()
    }

    override fun initScene() {
        materialLoader = RectMaterialLoader()
        loadMaterial()
        initMaterial()
        createMesh()

        renderable = EntityManager.get().create()

        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f))
            .geometry(
                0,
                RenderableManager.PrimitiveType.TRIANGLES,
                vertexBuffer,
                indexBuffer,
                0,
                6 * 6
            )
            .material(0, materialInstance)
            .build(engine, renderable)

        scene.addEntity(renderable)

        light = EntityManager.get().create()
        val (r, g, b) = Colors.cct(5_500.0f)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(r, g, b)
            .intensity(110_000.0f)
            .direction(0.0f, -0.5f, -1.0f)
            .castShadows(true)
            .build(engine, light)

        scene.addEntity(light)

        camera.setExposure(16.0f, 1.0f / 125.0f, 100.0f)
        camera.lookAt(0.0, 3.0, 4.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        startAnimation()
    }

    override fun destroyOthers() {
        engine.destroyMaterialInstance(materialInstance)
        engine.destroyVertexBuffer(vertexBuffer)
        engine.destroyIndexBuffer(indexBuffer)
        engine.destroyMaterial(material)
    }

    override fun getSurfaceCallback(): UiHelper.RendererCallback {
        return object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                swapChain?.let { engine.destroySwapChain(it) }
                swapChain = engine.createSwapChain(surface, uiHelper.swapChainFlags)
                displayHelper.attach(renderer, surfaceView.display)
            }

            override fun onDetachedFromSurface() {
                displayHelper.detach()
                swapChain?.let {
                    engine.destroySwapChain(it)
                    // Required to ensure we don't return before Filament is done executing the
                    // destroySwapChain command, otherwise Android might destroy the Surface
                    // too early
                    engine.flushAndWait()
                    swapChain = null
                }
            }

            override fun onResized(width: Int, height: Int) {
                val aspect = width.toDouble() / height.toDouble()
                camera.setProjection(45.0, aspect, 0.1, 20.0, Camera.Fov.VERTICAL)

                view.viewport = Viewport(0, 0, width, height)
            }
        }
    }

    private fun loadMaterial() {
        readUncompressAsset(materialLoader.getResource()).let {
            material = Material.Builder().payload(it, it.remaining()).build(engine)
        }
    }

    private fun initMaterial() {
        materialInstance = material.createInstance()
        materialInstance.setParameter("baseColor", Colors.RgbType.SRGB, 1.0f, 0.85f, 0.57f)
        materialInstance.setParameter("metallic", 0.0f)
        materialInstance.setParameter("roughness", 0.3f)
    }

    private fun startAnimation() {
        // Animate the triangle
        animator.interpolator = LinearInterpolator()
        animator.duration = 6000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
            val transformMatrix = FloatArray(16)
            override fun onAnimationUpdate(a: ValueAnimator) {
                Matrix.setRotateM(transformMatrix, 0, a.animatedValue as Float, 0.0f, 1.0f, 0.0f)
                val tcm = engine.transformManager
                tcm.setTransform(tcm.getInstance(renderable), transformMatrix)
            }
        })
        animator.start()
    }

    private fun createMesh() {
        val floatSize = 4
        val shortSize = 2
        // A vertex is a position + a tangent frame:
        // 3 floats for XYZ position, 4 floats for normal+tangents (quaternion)
        val vertexSize = 3 * floatSize + 4 * floatSize

        // Define a vertex and a function to put a vertex in a ByteBuffer
        @Suppress("ArrayInDataClass")
        data class Vertex(val x: Float, val y: Float, val z: Float, val tangents: FloatArray)

        fun ByteBuffer.put(v: Vertex): ByteBuffer {
            putFloat(v.x)
            putFloat(v.y)
            putFloat(v.z)
            v.tangents.forEach { putFloat(it) }
            return this
        }

        // 6 faces, 4 vertices per face
        val vertexCount = 6 * 4

        // Create tangent frames, one per face
        val tfPX = FloatArray(4)
        val tfNX = FloatArray(4)
        val tfPY = FloatArray(4)
        val tfNY = FloatArray(4)
        val tfPZ = FloatArray(4)
        val tfNZ = FloatArray(4)

        MathUtils.packTangentFrame(0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f, 0.0f, tfPX)
        MathUtils.packTangentFrame(0.0f, 1.0f, 0.0f, 0.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f, tfNX)
        MathUtils.packTangentFrame(-1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f, tfPY)
        MathUtils.packTangentFrame(-1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, -1.0f, 0.0f, tfNY)
        MathUtils.packTangentFrame(0.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, tfPZ)
        MathUtils.packTangentFrame(0.0f, -1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, tfNZ)

        val vertexData = ByteBuffer.allocate(vertexCount * vertexSize)
            // It is important to respect the native byte order
            .order(ByteOrder.nativeOrder())
            // Face -Z
            .put(Vertex(-1.0f, -1.0f, -1.0f, tfNZ))
            .put(Vertex(-1.0f, 1.0f, -1.0f, tfNZ))
            .put(Vertex(1.0f, 1.0f, -1.0f, tfNZ))
            .put(Vertex(1.0f, -1.0f, -1.0f, tfNZ))
            // Face +X
            .put(Vertex(1.0f, -1.0f, -1.0f, tfPX))
            .put(Vertex(1.0f, 1.0f, -1.0f, tfPX))
            .put(Vertex(1.0f, 1.0f, 1.0f, tfPX))
            .put(Vertex(1.0f, -1.0f, 1.0f, tfPX))
            // Face +Z
            .put(Vertex(-1.0f, -1.0f, 1.0f, tfPZ))
            .put(Vertex(1.0f, -1.0f, 1.0f, tfPZ))
            .put(Vertex(1.0f, 1.0f, 1.0f, tfPZ))
            .put(Vertex(-1.0f, 1.0f, 1.0f, tfPZ))
            // Face -X
            .put(Vertex(-1.0f, -1.0f, 1.0f, tfNX))
            .put(Vertex(-1.0f, 1.0f, 1.0f, tfNX))
            .put(Vertex(-1.0f, 1.0f, -1.0f, tfNX))
            .put(Vertex(-1.0f, -1.0f, -1.0f, tfNX))
            // Face -Y
            .put(Vertex(-1.0f, -1.0f, 1.0f, tfNY))
            .put(Vertex(-1.0f, -1.0f, -1.0f, tfNY))
            .put(Vertex(1.0f, -1.0f, -1.0f, tfNY))
            .put(Vertex(1.0f, -1.0f, 1.0f, tfNY))
            // Face +Y
            .put(Vertex(-1.0f, 1.0f, -1.0f, tfPY))
            .put(Vertex(-1.0f, 1.0f, 1.0f, tfPY))
            .put(Vertex(1.0f, 1.0f, 1.0f, tfPY))
            .put(Vertex(1.0f, 1.0f, -1.0f, tfPY))
            // Make sure the cursor is pointing in the right place in the byte buffer
            .flip()

        // Declare the layout of our mesh
        vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            // Because we interleave position and color data we must specify offset and stride
            // We could use de-interleaved data by declaring two buffers and giving each
            // attribute a different buffer index
            .attribute(
                VertexBuffer.VertexAttribute.POSITION,
                0,
                VertexBuffer.AttributeType.FLOAT3,
                0,
                vertexSize
            )
            .attribute(
                VertexBuffer.VertexAttribute.TANGENTS,
                0,
                VertexBuffer.AttributeType.FLOAT4,
                3 * floatSize,
                vertexSize
            )
            .build(engine)

        // Feed the vertex data to the mesh
        // We only set 1 buffer because the data is interleaved
        vertexBuffer.setBufferAt(engine, 0, vertexData)

        // Create the indices
        val indexData = ByteBuffer.allocate(6 * 2 * 3 * shortSize)
            .order(ByteOrder.nativeOrder())
        repeat(6) {
            val i = (it * 4).toShort()
            indexData
                .putShort(i).putShort((i + 1).toShort()).putShort((i + 2).toShort())
                .putShort(i).putShort((i + 2).toShort()).putShort((i + 3).toShort())
        }
        indexData.flip()

        // 6 faces, 2 triangles per face,
        indexBuffer = IndexBuffer.Builder()
            .indexCount(vertexCount * 2)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        indexBuffer.setBuffer(engine, indexData)
    }
}