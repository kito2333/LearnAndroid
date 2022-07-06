package com.example.helloworld.loader

class TriangleMaterialLoader: FilamentMaterialLoader {
    override fun getResource(): String {
        return "materials/baked_color.filamat"
    }
}