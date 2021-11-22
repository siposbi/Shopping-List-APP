package hu.bme.aut.android.sharedshoppinglist.util

import android.content.Context
import hu.bme.aut.android.sharedshoppinglist.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.asDateTimeString(context: Context): String {
    return this.format(DateTimeFormatter.ofPattern(context.getString(R.string.date_time_format)))
}

fun LocalDateTime.asDateString(context: Context): String {
    return this.format(DateTimeFormatter.ofPattern(context.getString(R.string.date_format)))
}