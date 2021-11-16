package hu.bme.aut.android.sharedshoppinglist.network

import android.os.Handler
import android.os.Looper
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import hu.bme.aut.android.sharedshoppinglist.ShoppingListApplication
import hu.bme.aut.android.sharedshoppinglist.model.*
import hu.bme.aut.android.sharedshoppinglist.network.apis.AuthAPI
import hu.bme.aut.android.sharedshoppinglist.network.apis.ProductAPI
import hu.bme.aut.android.sharedshoppinglist.network.apis.ShoppingListAPI
import hu.bme.aut.android.sharedshoppinglist.network.model.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime
import kotlin.concurrent.thread

class ShoppingListClient {
    companion object {
        const val BASE_URL = "https://shoppinglistapipz1pqy.azurewebsites.net/api/"
    }

    private val authApi: AuthAPI
    private val shoppingListApi: ShoppingListAPI
    private val productApi: ProductAPI
    private val sessionManager = ShoppingListApplication.sessionManager

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

        authApi = retrofit.create(AuthAPI::class.java)
        shoppingListApi = retrofit.create(ShoppingListAPI::class.java)
        productApi = retrofit.create(ProductAPI::class.java)
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

    fun authLogin(
        loginModel: LoginModel,
        onSuccess: (TokenModel) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = authApi.login(loginModel)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun authRegister(
        registerModel: RegisterModel,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = authApi.register(registerModel)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun authRefreshToken(
        tokenModel: RefreshTokenModel,
        onSuccess: (TokenModel) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = authApi.refreshToken(tokenModel)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListGetListsForUser(
        onSuccess: (List<ShoppingList>) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.getAllForUser()
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListGet(
        shareCode: String,
        onSuccess: (ShoppingList) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.join(shareCode)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListLeave(
        listId: Long,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.leave(listId)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListRename(
        listId: Long,
        newName: String,
        onSuccess: (ShoppingList) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.rename(listId, newName)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListCreate(
        name: String,
        onSuccess: (ShoppingList) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.create(name)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListGetMembers(
        listId: Long,
        onSuccess: (List<Member>) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.getMembers(listId)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun shoppingListGetExport(
        listId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        onSuccess: (List<Export>) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = shoppingListApi.getExport(listId, startDate, endDate)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun productGet(
        productId: Long,
        onSuccess: (Product) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = productApi.get(productId)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun productCreate(
        newProduct: ProductCreateModel,
        onSuccess: (ProductMinimal) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = productApi.create(newProduct)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun productGetAllOfList(
        listId: Long,
        onSuccess: (List<ProductMinimal>) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = productApi.getForList(listId)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun productDelete(
        productId: Long,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = productApi.delete(productId)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun productUndoDelete(
        productId: Long,
        onSuccess: (ProductMinimal) -> Unit,
        onError: (String) -> Unit
    ) {
        val apiRequest = productApi.undoDelete(productId)
        runCallOnBackgroundThread(apiRequest, onSuccess, onError)
    }

    fun productBuy(
        productId: Long,
        price: Long,
        onSuccess: (ProductMinimal) -> Unit,
        onError: (String) -> Unit
    ) {
        val getProductsRequest = productApi.buy(productId, price)
        runCallOnBackgroundThread(getProductsRequest, onSuccess, onError)
    }

    fun productUndoBuy(
        productId: Long,
        onSuccess: (ProductMinimal) -> Unit,
        onError: (String) -> Unit
    ) {
        val getProductsRequest = productApi.undoBuy(productId)
        runCallOnBackgroundThread(getProductsRequest, onSuccess, onError)
    }

    fun productUpdate(
        productId: Long,
        newProduct: ProductUpdateModel,
        onSuccess: (ProductMinimal) -> Unit,
        onError: (String) -> Unit
    ) {
        val getProductsRequest = productApi.update(productId, newProduct)
        runCallOnBackgroundThread(getProductsRequest, onSuccess, onError)
    }
}