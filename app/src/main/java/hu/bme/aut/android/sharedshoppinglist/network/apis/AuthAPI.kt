package hu.bme.aut.android.sharedshoppinglist.network.apis

import hu.bme.aut.android.sharedshoppinglist.network.LoginModel
import hu.bme.aut.android.sharedshoppinglist.network.RegisterModel
import hu.bme.aut.android.sharedshoppinglist.network.ResponseModel
import hu.bme.aut.android.sharedshoppinglist.network.TokenModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI {
    companion object {
        const val AUTH = "Auth"
    }

    @POST("$AUTH/login")
    fun login(@Body request: LoginModel): Call<ResponseModel<TokenModel>>

    @POST("$AUTH/register")
    fun register(@Body registerModel: RegisterModel): Call<ResponseModel<Long>>
}