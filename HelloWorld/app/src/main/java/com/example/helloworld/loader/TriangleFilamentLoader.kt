package com.example.helloworld.loader

import android.animation.ValueAnimator
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import android.view.animation.LinearInterpolator
import com.example.helloworld.ui.FilamentTestActivity
import com.google.android.filament.*
import com.google.android.filament.android.UiHelper
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class TriangleFilamentLoader : FilamentLoader() {
    override fun initScene() {
        materialLoader = TriangleMaterialLoader()
        loadMaterial()
        createMesh()

        renderable = EntityManager.get().create()

        RenderableManager.Builder(1)
            .boundingBox(Box(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.01f))
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vertexBuffer, indexBuffer, 0, 3)
            .material(0, material.defaultInstance)
            .build(engine, renderable)

        scene.addEntity(renderable)
        startAnimation()
    }

    override fun getSurfaceCallback(): UiHelper.RendererCallback {
        return object : UiHelper.RendererCallback {
            override fun onNativeWindowChanged(surface: Surface) {
                swapChain?.let { engine.destroySwapChain(it) }
                swapChain = engine.createSwapChain(surface, uiHelper.swapChainFlags)
                displayHelper.attach(renderer, surfaceView.display)
            }

            override fun onDetachedFromSurface() {
                Log.d(FilamentTestActivity.TAG, "onDetachedFromSurface")
                displayHelper.detach()
                swapChain?.let {
                    engine.destroySwapChain(it)
                    engine.flushAndWait()
                    swapChain = null
                }
            }

            override fun onResized(width: Int, height: Int) {
                Log.d(FilamentTestActivity.TAG, "onResized w: $width, h $height")
                val zoom = 1.5
                val aspect = width.toDouble() / height.toDouble()
                camera.setProjection(
                    Camera.Projection.ORTHO,
                    -aspect * zoom, aspect * zoom, -zoom, zoom, 0.0, 10.0
                )
                view.viewport = Viewport(0, 0, width, height)
            }
        }
    }

    private fun loadMaterial() {
        readUncompressAsset(TriangleMaterialLoader().getResource()).let {
            material = Material.Builder().payload(it, it.remaining()).build(engine)
        }
    }

    private fun createMesh() {
        val intSize = 4
        val floatSize = 4
        val shortSize = 2
        val vertexSize = 3 * floatSize + intSize

        data class Vertex(val x: Float, val y: Float, val z: Float, val color: Int)

        fun ByteBuffer.put(v: Vertex): ByteBuffer {
            putFloat(v.x)
            putFloat(v.y)
            putFloat(v.z)
            putInt(v.color)
            return this
        }

        val vertexCount = 3
        val a1 = PI * 2.0 / 3.0
        val a2 = PI * 4.0 / 3.0

        val vertexData = ByteBuffer.allocate(vertexCount * vertexSize)
            .order(ByteOrder.nativeOrder())
            .put(Vertex(1.0f, 0.0f, 0.0f, 0xffff0000.toInt()))
            .put(Vertex(cos(a1).toFloat(), sin(a1).toFloat(), 0.0f, 0xff00ff00.toInt()))
            .put(Vertex(cos(a2).toFloat(), sin(a2).toFloat(), 0.0f, 0xff0000ff.toInt()))
            .flip()

        vertexBuffer = VertexBuffer.Builder()
            .bufferCount(1)
            .vertexCount(vertexCount)
            .attribute(
                VertexBuffer.VertexAttribute.POSITION,
                0,
                VertexBuffer.AttributeType.FLOAT3,
                0,
                vertexSize
            )
            .attribute(
                VertexBuffer.VertexAttribute.COLOR,
                0,
                VertexBuffer.AttributeType.UBYTE4,
                3 * floatSize,
                vertexSize
            )
            .normalized(VertexBuffer.VertexAttribute.COLOR)
            .build(engine)
        vertexBuffer.setBufferAt(engine, 0, vertexData)

        val indexData = ByteBuffer.allocate(vertexCount * shortSize)
            .order(ByteOrder.nativeOrder())
            .putShort(0)
            .putShort(1)
            .putShort(2)
            .flip()

        indexBuffer = IndexBuffer.Builder()
            .indexCount(3)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        indexBuffer.setBuffer(engine, indexData)
    }

    private fun startAnimation() {
        animator.apply {
            interpolator = LinearInterpolator()
            duration = 4000
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                val transformMatrix = FloatArray(16)
                override fun onAnimationUpdate(animation: ValueAnimator?) {
                    Matrix.setRotateM(
                        transformMatrix,
                        0,
                        -(animation?.animatedValue as Float),
                        0.0f,
                        0.0f,
                        1.0f
                    )
                    val tcm = engine.transformManager
                    tcm.setTransform(tcm.getInstance(renderable), transformMatrix)
                }
            })
        }.start()
    }
}