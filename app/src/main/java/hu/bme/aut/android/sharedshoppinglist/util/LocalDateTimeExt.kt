package hu.bme.aut.android.sharedshoppinglist.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val LocalDateTime.asDateTimeString: String
    get() = this.format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))

val LocalDateTime.asDateString: String
    get() = this.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))