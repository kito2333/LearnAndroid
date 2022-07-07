package com.example.helloworld

import com.example.helloworld.loader.BaseLightingFilamentLoader
import com.example.helloworld.loader.FilamentLoader
import com.example.helloworld.loader.RectFilamentLoader
import com.example.helloworld.loader.TriangleFilamentLoader

object FilamentFactory {
    enum class RenderType(val text: String) {
        TRIANGLE("TRIANGLE"), RECT("RECT"), IBL("IBL");

        companion object {
            fun fromString(text: String): RenderType {
                return when (text) {
                    "RECT" -> RECT
                    "IBL" -> IBL
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
        }
    }
}