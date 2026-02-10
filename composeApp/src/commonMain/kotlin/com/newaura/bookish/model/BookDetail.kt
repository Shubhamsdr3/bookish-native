package com.newaura.bookish.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
    val language: String? = null
)

@Serializable
data class ImageLinks(
    @SerialName("smallThumbnail")
    val smallThumbnail: String? = null,
    @SerialName("thumbnail")
    val thumbnail: String? = null
)

