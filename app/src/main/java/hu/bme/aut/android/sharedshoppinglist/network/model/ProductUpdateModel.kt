package hu.bme.aut.android.sharedshoppinglist.network.model

data class ProductUpdateModel(
    val name: String,
    val price: Long,
    val isShared: Boolean
)