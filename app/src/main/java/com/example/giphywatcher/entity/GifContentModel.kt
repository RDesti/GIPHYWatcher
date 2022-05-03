package com.example.giphywatcher.entity

data class GifContentModel(
    var id: String?,
    var name: String?,
    var url: String?
) {
    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) {
            return false
        }
        other as GifContentModel
        if (id != other.id) {
            return false
        }
        if (name != other.name) {
            return false
        }
        if (url != other.url) {
            return false
        }
        return true
    }
}
