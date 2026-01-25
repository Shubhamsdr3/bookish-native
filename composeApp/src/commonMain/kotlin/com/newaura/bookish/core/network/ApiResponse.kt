package com.newaura.bookish.core.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    var data: T? = null,
    var status: ApiStatus = ApiStatus.LOADING,
    var message: String = ""
) {
    fun copyWith(
        status: ApiStatus? = null,
        data: T? = null,
        message: String? = null
    ): ApiResponse<T> {
        return ApiResponse(
            status = status ?: this.status,
            data = data ?: this.data,
            message = message ?: this.message
        )
    }

    fun set(status: ApiStatus, data: T? = null, message: String = "") {
        this.data = data
        this.status = status
        this.message = message
    }

    val isSuccess: Boolean
        get() = status == ApiStatus.SUCCESS

    override fun toString(): String = message
}

enum class ApiStatus {
    LOADING,
    SUCCESS,
    ERROR
}
