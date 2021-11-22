package hu.bme.aut.android.sharedshoppinglist.network.apis

import hu.bme.aut.android.sharedshoppinglist.model.Product
import hu.bme.aut.android.sharedshoppinglist.model.ProductMinimal
import hu.bme.aut.android.sharedshoppinglist.network.model.ProductCreateModel
import hu.bme.aut.android.sharedshoppinglist.network.model.ProductUpdateModel
import hu.bme.aut.android.sharedshoppinglist.network.model.ResponseModel
import retrofit2.Call
import retrofit2.http.*

interface ProductAPI {
    companion object {
        const val PRODUCTS = "Product"
    }

    @GET("${PRODUCTS}/{productId}")
    fun get(@Path("productId") productId: Long): Call<ResponseModel<Product>>

    @POST("${PRODUCTS}/create")
    fun create(@Body newProduct: ProductCreateModel): Call<ResponseModel<ProductMinimal>>

    @GET("${PRODUCTS}/getAllForList/{listId}")
    fun getForList(@Path("listId") listId: Long): Call<ResponseModel<List<ProductMinimal>>>

    @DELETE("${PRODUCTS}/delete/{listId}")
    fun delete(@Path("listId") listId: Long): Call<ResponseModel<Long>>

    @PUT("${PRODUCTS}/undoDelete/{productId}")
    fun undoDelete(@Path("productId") productId: Long): Call<ResponseModel<ProductMinimal>>

    @PUT("${PRODUCTS}/buy/{productId}")
    fun buy(
        @Path("productId") productId: Long,
        @Body price: Long
    ): Call<ResponseModel<ProductMinimal>>

    @PUT("${PRODUCTS}/undoBuy/{productId}")
    fun undoBuy(@Path("productId") productId: Long): Call<ResponseModel<ProductMinimal>>

    @PUT("${PRODUCTS}/update/{productId}")
    fun update(
        @Path("productId") productId: Long,
        @Body updatedProduct: ProductUpdateModel
    ): Call<ResponseModel<ProductMinimal>>
}