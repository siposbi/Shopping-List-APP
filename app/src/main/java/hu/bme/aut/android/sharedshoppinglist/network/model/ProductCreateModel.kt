package hu.bme.aut.android.sharedshoppinglist.network.model

data class ProductCreateModel(
    val shoppingListId: Long,
    val name: String,
    val price: Long,
    val isShared: Boolean
)