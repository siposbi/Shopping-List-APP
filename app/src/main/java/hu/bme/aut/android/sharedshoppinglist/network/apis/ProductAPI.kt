package hu.bme.aut.android.sharedshoppinglist.network.apis

import hu.bme.aut.android.sharedshoppinglist.model.ShoppingList
import hu.bme.aut.android.sharedshoppinglist.network.ResponseModel
import retrofit2.Call
import retrofit2.http.GET

interface ProductAPI {
    companion object{
        const val PRODUCTS = "Product"
    }


}