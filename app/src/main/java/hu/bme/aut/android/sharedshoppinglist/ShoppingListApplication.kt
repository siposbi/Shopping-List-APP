package hu.bme.aut.android.sharedshoppinglist

import android.app.Application
import androidx.room.Room
import hu.bme.aut.android.sharedshoppinglist.database.ShoppingListDatabase
import hu.bme.aut.android.sharedshoppinglist.network.SessionManager
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient

class ShoppingListApplication : Application() {

    companion object {
        lateinit var shoppingListDatabase: ShoppingListDatabase
            private set
        lateinit var apiClient: ShoppingListClient
            private set
        lateinit var sessionManager: SessionManager
            private set
    }

    override fun onCreate() {
        super.onCreate()

        shoppingListDatabase = Room.databaseBuilder(
            applicationContext,
            ShoppingListDatabase::class.java,
            "shopping_list_database"
        ).fallbackToDestructiveMigration().build()

        sessionManager = SessionManager(applicationContext)
        apiClient = ShoppingListClient()
    }

}