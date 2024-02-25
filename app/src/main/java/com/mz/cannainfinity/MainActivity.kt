package com.mz.cannainfinity

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mz.cannainfinity.data.MongoDB
import com.mz.cannainfinity.navigation.Screen
import com.mz.cannainfinity.navigation.SetupNavGraph
import com.mz.cannainfinity.ui.theme.CannaInfinityTheme
import com.mz.cannainfinity.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        MongoDB.configureTheRealm()
        setContent {
            CannaInfinityTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController)
            }
        }
    }
}


private fun getStartDestination(): String {
    val user = App.create(APP_ID).currentUser
    return if (user!= null&& user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}