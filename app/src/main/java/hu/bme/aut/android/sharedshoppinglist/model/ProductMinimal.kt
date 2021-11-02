package hu.bme.aut.android.sharedshoppinglist.model

data class ProductMinimal(
    val id: Long,
    val name: String,
    val addedByUserFirstName: String,
    val addedByUserLastName: String,
    val price: Long,
    val isShared: Boolean,
    val isBought: Boolean
)