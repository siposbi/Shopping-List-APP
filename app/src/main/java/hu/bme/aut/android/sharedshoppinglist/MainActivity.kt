package hu.bme.aut.android.sharedshoppinglist

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import hu.bme.aut.android.sharedshoppinglist.databinding.ActivityMainBinding
import hu.bme.aut.android.sharedshoppinglist.util.USER_LOGGED_IN_KEY


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isUserLoggedIn = getPreferences(Context.MODE_PRIVATE).getBoolean(USER_LOGGED_IN_KEY, false)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_graph)
        if (isUserLoggedIn){
            graph.startDestination = R.id.shoppingListFragment
        } else {
            graph.startDestination = R.id.loginFragment
        }
        navHostFragment.navController.graph = graph
        supportFragmentManager.beginTransaction().setPrimaryNavigationFragment(navHostFragment).commit()
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration.Builder(R.id.loginFragment, R.id.shoppingListFragment).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}