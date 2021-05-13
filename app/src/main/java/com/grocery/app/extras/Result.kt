package com.grocery.app.extras

data class Result<out T>(val type: Status, val data: T?, val exception: Exception?) {

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
                null
            )
        }

        fun <T> error(exp: Exception? = null, data: T? = null): Result<T> {
            return Result(
                Status.ERROR,
                data,
                exp
            )
        }

        fun <T> loading(data: T? = null): Result<T> {
            return Result(
                Status.LOADING,
                data,
                null
            )
        }
    }
}