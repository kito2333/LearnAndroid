package com.example.helloworld.loader

class TriangleMaterialLoader: FilamentMaterialLoader {
    override fun getResource(): String {
        return "baked_color.filamat"
    }
}