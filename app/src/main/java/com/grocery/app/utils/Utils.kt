package com.grocery.app.utils


fun String?.isBlank(): Boolean {
    return this.isNullOrBlank()
}