package hu.bme.aut.android.sharedshoppinglist.network

data class ResponseModel<T>(
    val isSuccess: Boolean,
    val message: String,
    val data: T?
)