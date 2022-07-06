package com.example.helloworld

import com.example.helloworld.loader.BaseLightingFilamentLoader
import com.example.helloworld.loader.FilamentLoader
import com.example.helloworld.loader.RectFilamentLoader
import com.example.helloworld.loader.TriangleFilamentLoader

object FilamentFactory {
    enum class Type {
        TRIANGLE, RECT, IBL
    }

    fun createFilamentLoader(type: Type): FilamentLoader {
        return when (type) {
            Type.TRIANGLE -> TriangleFilamentLoader()
            Type.RECT -> RectFilamentLoader()
            Type.IBL -> BaseLightingFilamentLoader()
        }
    }
}