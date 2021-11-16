package hu.bme.aut.android.sharedshoppinglist.network.apis

import hu.bme.aut.android.sharedshoppinglist.network.model.*
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

    @POST("$AUTH/refresh")
    fun refreshToken(@Body tokenModel: RefreshTokenModel): Call<ResponseModel<TokenModel>>
}