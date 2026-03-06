package com.newaura.bookish.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    var data: T? = null,
    var errorMessage: String = ""
) {

    fun copyWith(
        data: T? = null,
        message: String? = null
    ): ApiResponse<T> {
        return ApiResponse(
            data = data ?: this.data,
            errorMessage = message ?: this.errorMessage
        )
    }

    fun set(data: T? = null, message: String = "") {
        this.data = data
        this.errorMessage = message
    }

    val isSuccess: Boolean
        get() = data != null && errorMessage.isEmpty()

    override fun toString(): String = errorMessage
}
