package com.newaura.bookish.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookDetailResponse(
    val isSuccess: Boolean? = null,
    val data: BookDetail? = null
)


@Serializable
data class BookDetail(
    @SerialName("id")
    val id: String,
    @SerialName("volumeInfo")
    val volumeInfo: VolumeInfo? = null
)

@Serializable
data class VolumeInfo(
    @SerialName("title")
    val title: String? = null,
    @SerialName("authors")
    val authors: List<String>? = null,
    @SerialName("publisher")
    val publisher: String? = null,
    @SerialName("publishedDate")
    val publishedDate: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("pageCount")
    val pageCount: Int? = null,
    @SerialName("categories")
    val categories: List<String>? = null,
    @SerialName("imageLinks")
    val imageLinks: ImageLinks? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("averageRating")
    val avgRating: Double? = null,
    @SerialName("ratingCound")
    val ratingCount: Double? = null,
)

@Serializable
data class ImageLinks(
    @SerialName("smallThumbnail")
    val smallThumbnail: String? = null,
    @SerialName("thumbnail")
    val thumbnail: String? = null
) {

    fun getSecureThumbnail(): String? {
        return thumbnail?.replace("http://", "https://")
    }

    fun getSecureSmallThumbnail(): String? {
        return smallThumbnail?.replace("http://", "https://")
    }
}

