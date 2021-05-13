package com.grocery.app.extensions

import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


fun Date.formatDate(format: String = "dd-MM-yyyy"): String {
    return try {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        sdf.format(this)
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}

fun Date.getDuration(endTime: Date): String {
    val milliSec = endTime.time - this.time
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSec)
    if (minutes <= 0) {
        return "0m"
    }
    val builder = StringBuilder()
    val days = TimeUnit.MILLISECONDS.toDays(milliSec)
    val hours = TimeUnit.MILLISECONDS.toHours(milliSec)
    if (days > 0) {
        builder.append(days).append("d")
    }
    if (hours % 24 > 0) {
        builder.append(" ").append(hours % 24).append("h")
    }
    if (minutes % 60 > 0) {
        builder.append(" ").append(minutes % 60).append("m")
    }
    return builder.toString()
}

fun String.parseDate(format: String = "dd-MM-yyyy"): Date {
    return try {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        sdf.parse(this) ?: currentDate
    } catch (e: Exception) {
        e.printStackTrace()
        Date()
    }
}

val currentDate: Date
    get() = Date()

val todayMidnight: Date
    get() = currentDate.midNight

val Date.midNight: Date
    get() = this.getNewTime(hourOfDay = 0, minute = 0, second = 0, milliSec = 0)

val Date.dayLastSecond
    get() = this.midNight.time + TimeUnit.DAYS.toMillis(1) - 1000


fun Date.getNewTime(
    hourOfDay: Int? = null,
    minute: Int? = null,
    second: Int? = null,
    milliSec: Int? = null,
    year: Int? = null,
    month: Int? = null,
    dayOfMonth: Int? = null
): Date {
    val calendar = Calendar.getInstance().apply { timeInMillis = this@getNewTime.time }
    hourOfDay?.let { calendar.set(Calendar.HOUR_OF_DAY, it) }
    minute?.let { calendar.set(Calendar.MINUTE, it) }
    second?.let { calendar.set(Calendar.SECOND, it) }
    milliSec?.let { calendar.set(Calendar.MILLISECOND, it) }
    year?.let { calendar.set(Calendar.YEAR, it) }
    month?.let { calendar.set(Calendar.MONTH, it) }
    dayOfMonth?.let { calendar.set(Calendar.DAY_OF_MONTH, it) }
    return Date(calendar.timeInMillis)
}

fun Date.age(): Int {
    val dob = Calendar.getInstance().apply { timeInMillis = this@age.time }
    val today = Calendar.getInstance()

    var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
    if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
        age--
    } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)) {
        if (today.get(Calendar.DAY_OF_MONTH) < dob.get(Calendar.DAY_OF_MONTH)) {
            age--
        }
    }
    return age
}