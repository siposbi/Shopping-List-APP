package hu.bme.aut.android.sharedshoppinglist.model

data class Export(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val money: Long
)