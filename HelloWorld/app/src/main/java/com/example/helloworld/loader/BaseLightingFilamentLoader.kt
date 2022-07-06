package com.example.helloworld.loader

import android.animation.ValueAnimator
import android.view.Surface
import android.view.animation.LinearInterpolator
import com.example.helloworld.data.*
import com.google.android.filament.*
import com.google.android.filament.android.UiHelper
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BaseLightingFilamentLoader : FilamentLoader() {
    private lateinit var mesh: Mesh
    private lateinit var ibl: Ibl

    override fun getValueAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0.0f, (2.0 * PI).toFloat())
    }

    override fun initView() {
        val ssaoOptions = view.ambientOcclusionOptions
        ssaoOptions.enabled = true
        view.ambientOcclusionOptions = ssaoOptions

        // NOTE: Try to disable post-processing (tone-mapping, etc.) to see the difference
        // view.isPostProcessingEnabled = false

        super.initView()
    }

    override fun initScene() {
        loadMaterial()
        setupMaterial()
        loadImageBaseLight()

        scene.skybox = ibl.skybox
        scene.indirectLight = ibl.indirectLight


        // This map can contain named materials that will map to the material names
        // loaded from the filamesh file. The material called "DefaultMaterial" is
        // applied when no named material can be found
        val materials = mapOf("DefaultMaterial" to materialInstance)

        // Load the mesh in the filamesh format (see filamesh tool)
        mesh = loadMesh(context.assets, "models/shader_ball.filamesh", materials, engine)

        // Move the mesh down
        // Filament uses column-major matrices
        engine.transformManager.setTransform(
            engine.transformManager.getInstance(mesh.renderable),
            floatArrayOf(
                1.0f, 0.0f, 0.0f, 0.0f,
                0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, -1.2f, 0.0f, 1.0f
            )
        )

        // Add the entity to the scene to render it
        scene.addEntity(mesh.renderable)

        // We now need a light, let's create a directional light
        light = EntityManager.get().create()

        // Create a color from a temperature (D65)
        val (r, g, b) = Colors.cct(6_500.0f)
        LightManager.Builder(LightManager.Type.DIRECTIONAL)
            .color(r, g, b)
            // Intensity of the sun in lux on a clear day
            .intensity(110_000.0f)
            // The direction is normalized on our behalf
            .direction(-0.753f, -1.0f, 0.890f)
            .castShadows(true)
            .build(engine, light)

        // Add the entity to the scene to light it
        scene.addEntity(light)

        // Set the exposure on the camera, this exposure follows the sunny f/16 rule
        // Since we've defined a light that has the same intensity as the sun, it
        // guarantees a proper exposure
        camera.setExposure(16.0f, 1.0f / 125.0f, 100.0f)

        startAnimation()
    }

    override fun destroyOthers() {
        destroyMesh(engine, mesh)
        destroyIbl(engine, ibl)
        engine.destroyMaterialInstance(materialInstance)
        engine.destroyMaterial(material)
    }

    override fun getSurfaceCallback(): UiHelper.RendererCallback {
        return object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                swapChain?.let { engine.destroySwapChain(it) }
                swapChain = engine.createSwapChain(surface)
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
        readUncompressAsset("materials/clear_coat.filamat").let {
            material = Material.Builder().payload(it, it.remaining()).build(engine)
        }
    }

    private fun setupMaterial() {
        // Create an instance of the material to set different parameters on it
        materialInstance = material.createInstance()

        // Specify that our color is in sRGB so the conversion to linear
        // is done automatically for us. If you already have a linear color
        // you can pass it directly, or use Colors.RgbType.LINEAR
        materialInstance.setParameter("baseColor", Colors.RgbType.SRGB, 0.71f, 0.0f, 0.0f)
    }

    private fun loadImageBaseLight() {
        ibl = loadIbl(context.assets, "envs/flower_road_no_sun_2k", engine)
        ibl.indirectLight.intensity = 40_000.0f
    }

    private fun startAnimation() {
        // Animate the triangle
        animator.interpolator = LinearInterpolator()
        animator.duration = 18_000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = ValueAnimator.INFINITE
        animator.addUpdateListener { a ->
            val v = (a.animatedValue as Float)
            camera.lookAt(cos(v) * 4.5, 1.5, sin(v) * 4.5, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        }
        animator.start()
    }
}