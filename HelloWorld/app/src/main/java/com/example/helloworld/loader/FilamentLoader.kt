package com.example.helloworld.loader

import android.animation.ValueAnimator
import android.content.Context
import android.view.Choreographer
import android.view.Surface
import android.view.SurfaceView
import com.google.android.filament.*
import com.google.android.filament.android.DisplayHelper
import com.google.android.filament.android.UiHelper
import java.nio.ByteBuffer
import java.nio.channels.Channels

open class FilamentLoader {
    lateinit var surfaceView: SurfaceView
    private val frameScheduler = FrameCallback()
    private lateinit var choreographer: Choreographer
    protected val animator: ValueAnimator = getValueAnimator()

    // for filament
    protected lateinit var uiHelper: UiHelper
    protected lateinit var displayHelper: DisplayHelper
    protected lateinit var renderer: Renderer
    protected lateinit var view: View
    protected lateinit var camera: Camera
    protected lateinit var engine: Engine
    protected lateinit var scene: Scene
    protected lateinit var material: Material
    protected lateinit var materialInstance: MaterialInstance
    protected lateinit var vertexBuffer: VertexBuffer
    protected lateinit var indexBuffer: IndexBuffer

    @Entity
    protected var renderable = 0

    @Entity
    protected var light = 0
    protected var swapChain: SwapChain? = null

    protected lateinit var context: Context

    fun init(context: Context) {
        this.context = context
        surfaceView = SurfaceView(context)
        choreographer = Choreographer.getInstance()

        displayHelper = DisplayHelper(context)

        initSurfaceView()
        initFilament()
        initView()
        initScene()
    }

    private fun initSurfaceView() {
        uiHelper = UiHelper(UiHelper.ContextErrorPolicy.DONT_CHECK)
        uiHelper.renderCallback = getSurfaceCallback()

        uiHelper.attachTo(surfaceView)
    }

    private fun initFilament() {
        engine = Engine.create()
        renderer = engine.createRenderer()
        scene = engine.createScene()
        view = engine.createView()
        camera = engine.createCamera(engine.entityManager.create())
    }

    protected fun readUncompressAsset(assetName: String): ByteBuffer {
        context.assets.openFd(assetName).use {
            val input = it.createInputStream()
            val dst = ByteBuffer.allocate(it.length.toInt())

            val src = Channels.newChannel(input)
            src.read(dst)
            src.close()

            return dst.apply { rewind() }
        }
    }

    fun resume() {
        choreographer.postFrameCallback(frameScheduler)
        animator.start()
    }

    fun pause() {
        choreographer.removeFrameCallback(frameScheduler)
        animator.cancel()
    }

    fun destroy() {
        choreographer.removeFrameCallback(frameScheduler)
        animator.cancel()

        uiHelper.detach()
        destroyOthers()
        engine.destroyEntity(renderable)
        engine.destroyEntity(light)
        engine.destroyRenderer(renderer)
        engine.destroyView(view)
        engine.destroyScene(scene)
        engine.destroyCameraComponent(camera.entity)

        val entityManager = EntityManager.get()
        entityManager.destroy(renderable)
        entityManager.destroy(light)
        entityManager.destroy(camera.entity)

        engine.destroy()
    }

    open fun initView() {
        // Tell the view which camera we want to use
        view.camera = camera

        // Tell the view which scene we want to render
        view.scene = scene
    }

    open fun getValueAnimator(): ValueAnimator {
        return ValueAnimator.ofFloat(0.0f, 360.0f)
    }

    open fun getSurfaceCallback(): UiHelper.RendererCallback {
        return object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface?) {}

            override fun onDetachedFromSurface() {}

            override fun onResized(width: Int, height: Int) {}
        }
    }

    open fun initScene() {}

    open fun destroyOthers() {

    }

    inner class FrameCallback : Choreographer.FrameCallback {
        override fun doFrame(frameTimeNanos: Long) {
            // Schedule the next frame
            choreographer.postFrameCallback(this)

            // This check guarantees that we have a swap chain
            if (uiHelper.isReadyToRender) {
                // If beginFrame() returns false you should skip the frame
                // This means you are sending frames too quickly to the GPU
                if (renderer.beginFrame(swapChain!!, frameTimeNanos)) {
                    renderer.render(view)
                    renderer.endFrame()
                }
            }
        }
    }
}