package com.newaura.bookish.features.post.data

data class ImageFile(
    val name: String,
    val path: String,
    val byteArray: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageFile) return false
        return name == other.name && path == other.path
    }

    override fun hashCode(): Int {
        return name.hashCode() + path.hashCode()
    }
}