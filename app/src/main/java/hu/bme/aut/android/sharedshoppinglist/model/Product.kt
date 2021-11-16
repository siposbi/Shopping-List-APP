package hu.bme.aut.android.sharedshoppinglist.model

import java.time.LocalDateTime

data class Product(
    val id: Long,
    val name: String,
    val addedByUserFirstName: String,
    val addedByUserLastName: String,
    val boughtByUserFirstName: String?,
    val boughtByUserLastName: String?,
    val createdDateTime: LocalDateTime,
    val boughtDateTime: LocalDateTime,
    val price: Long,
    val isShared: Boolean
)