package hu.bme.aut.android.sharedshoppinglist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import hu.bme.aut.android.sharedshoppinglist.databinding.ActivityMainBinding
import hu.bme.aut.android.sharedshoppinglist.fragment.ShoppingListFragmentDirections
import hu.bme.aut.android.sharedshoppinglist.network.SessionManager
import hu.bme.aut.android.sharedshoppinglist.network.ShoppingListClient
import hu.bme.aut.android.sharedshoppinglist.network.model.TokenModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var apiClient: ShoppingListClient
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiClient = ShoppingListClient(this)
        sessionManager = SessionManager(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        if (sessionManager.getUserLoggedIn() && sessionManager.getAuthTokenValid()) {
            graph.startDestination = R.id.shoppingListFragment
        } else if (sessionManager.getRefreshAuthTokenValid()) {
            apiClient.refresh(
                tokenModel = sessionManager.getRefreshTokenModel(),
                onSuccess = ::onRefreshTokenSuccess,
                onError = ::tokenRefreshFail
            )
            graph.startDestination = R.id.shoppingListFragment
        } else {
            sessionManager.logoutUser()
            graph.startDestination = R.id.loginFragment
        }
        navHostFragment.navController.graph = graph
        supportFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment)
            .commit()
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        val appBarConfiguration =
            AppBarConfiguration.Builder(R.id.loginFragment, R.id.shoppingListFragment).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun onRefreshTokenSuccess(tokenModel: TokenModel){
        sessionManager.loginUser(tokenModel)
    }

    private fun tokenRefreshFail(error: String){
        sessionManager.logoutUser()
        Snackbar.make(binding.root, error, Snackbar.LENGTH_LONG).show()
        val action = ShoppingListFragmentDirections.actionShoppingListFragmentToLoginFragment()
        navController.navigate(action)
    }
}