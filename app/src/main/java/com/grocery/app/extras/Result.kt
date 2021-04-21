package com.grocery.app.extras

data class Result<out T>(val type: Status, val data: T?, val message: String?, val status: Int?) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> success(data: T? = null): Result<T> {
            return Result(
                Status.SUCCESS,
                data,
                null,
                200
            )
        }

        fun <T> error(status: Int? = -1, message: String? = null, data: T? = null): Result<T> {
            return Result(
                Status.ERROR,
                data,
                message,
                status
            )
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(
                Status.LOADING,
                data,
                null,
                null
            )
        }
    }
}