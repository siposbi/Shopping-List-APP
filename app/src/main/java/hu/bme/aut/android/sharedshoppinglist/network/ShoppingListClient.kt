package hu.bme.aut.android.sharedshoppinglist.network

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListAPI.Companion.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.concurrent.thread

class ShoppingListClient(context: Context) {
    private val apiService: ShoppingListAPI
    var sessionManager: SessionManager = SessionManager(context)

    init {
        val moshi = Moshi.Builder()
            .add(LocalDateTimeAdapter())
            .addLast(KotlinJsonAdapterFactory())
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(OkHttpClient.Builder().addInterceptor(AuthInterceptor(sessionManager)).build())
            .build()

        apiService = retrofit.create(ShoppingListAPI::class.java)
    }

    private fun <T> runCallOnBackgroundThread(
        call: Call<ResponseModel<T>>,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper()!!)
        thread {
            try {
                val response = call.execute().body()!!
                if (response.isSuccess) {
                    handler.post { onSuccess(response.data!!) }
                } else {
                    handler.post { onError(response.message) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                handler.post { onError(e.message ?: "Unexpected error") }
            }
        }
    }

    fun login(
        loginModel: LoginModel,
        onSuccess: (TokenModel) -> Unit,
        onError: (String) -> Unit
    ) {
        val loginRequest = apiService.login(loginModel)
        runCallOnBackgroundThread(loginRequest, onSuccess, onError)
    }

    fun register(
        registerModel: RegisterModel,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val registerRequest = apiService.register(registerModel)
        runCallOnBackgroundThread(registerRequest, onSuccess, onError)
    }

    fun getShoppingLists(
        onSuccess: (List<ShoppingList>) -> Unit,
        onError: (String) -> Unit
    ) {
        val getShoppingListsRequest = apiService.getShoppingLists()
        runCallOnBackgroundThread(getShoppingListsRequest, onSuccess, onError)
    }
}