package com.example.helloworld.loader

import android.animation.ValueAnimator
import android.view.Surface
import android.view.animation.LinearInterpolator
import com.google.android.filament.*
import com.google.android.filament.textured.TextureType
import com.google.android.filament.textured.loadTexture
import com.example.helloworld.R
import com.example.helloworld.data.*
import com.google.android.filament.android.UiHelper
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class BaseTextureFilamentLoader : FilamentLoader() {
    private lateinit var baseColor: Texture
    private lateinit var normal: Texture
    private lateinit var aoRoughnessMetallic: Texture

    private lateinit var mesh: Mesh
    private lateinit var ibl: Ibl

    override fun getValueAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0.0f, (2.0 * PI).toFloat())
    }

    override fun initView() {
        super.initView()

        val options = View.DynamicResolutionOptions()
        options.enabled = true

        view.dynamicResolutionOptions = options
    }

    override fun initScene() {
        loadMaterial()
        setupMaterial()
        loadImageBasedLight()

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
                0.0f, -100.0f, 0.0f, 1.0f
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
        engine.destroyTexture(baseColor)
        engine.destroyTexture(normal)
        engine.destroyTexture(aoRoughnessMetallic)
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
        readUncompressAsset("materials/textured_pbr.filamat").let {
            material = Material.Builder().payload(it, it.remaining()).build(engine)
        }
    }

    private fun setupMaterial() {
        // Create an instance of the material to set different parameters on it
        materialInstance = material.createInstance()
        val resources = context.resources

        // Note that the textures are stored in drawable-nodpi to prevent the system
        // from automatically resizing them based on the display's density
        baseColor = loadTexture(engine, resources, R.drawable.floor_basecolor, TextureType.COLOR)
        normal = loadTexture(engine, resources, R.drawable.floor_normal, TextureType.NORMAL)
        aoRoughnessMetallic = loadTexture(
            engine, resources,
            R.drawable.floor_ao_roughness_metallic, TextureType.DATA
        )

        // A texture sampler does not need to be kept around or destroyed
        val sampler = TextureSampler()
        sampler.anisotropy = 8.0f

        materialInstance.setParameter("baseColor", baseColor, sampler)
        materialInstance.setParameter("normal", normal, sampler)
        materialInstance.setParameter("aoRoughnessMetallic", aoRoughnessMetallic, sampler)
    }

    private fun loadImageBasedLight() {
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
            camera.lookAt(cos(v) * 800.0, 0.0, sin(v) * 800.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
        }
        animator.start()
    }
}