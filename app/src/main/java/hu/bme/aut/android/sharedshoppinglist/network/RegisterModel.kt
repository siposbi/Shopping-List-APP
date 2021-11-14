package hu.bme.aut.android.sharedshoppinglist.network

data class RegisterModel(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)