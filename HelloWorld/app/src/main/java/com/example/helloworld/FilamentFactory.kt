package com.example.helloworld

import com.example.helloworld.loader.*

object FilamentFactory {
    enum class RenderType(val text: String) {
        TRIANGLE("TRIANGLE"), RECT("RECT"), IBL("IBL"), IBT("IBT");

        companion object {
            fun fromString(text: String): RenderType {
                return when (text) {
                    "RECT" -> RECT
                    "IBL" -> IBL
                    "IBT" -> IBT
                    else -> TRIANGLE
                }
            }
        }
    }

    fun createFilamentLoader(type: RenderType): FilamentLoader {
        return when (type) {
            RenderType.TRIANGLE -> TriangleFilamentLoader()
            RenderType.RECT -> RectFilamentLoader()
            RenderType.IBL -> BaseLightingFilamentLoader()
            RenderType.IBT -> BaseTextureFilamentLoader()
        }
    }
}