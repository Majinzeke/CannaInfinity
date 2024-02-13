package com.mz.cannainfinity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.mz.cannainfinity.navigation.Screen
import com.mz.cannainfinity.navigation.SetupNavGraph
import com.mz.cannainfinity.ui.theme.CannaInfinityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            CannaInfinityTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                SetupNavGraph(startDestination = Screen.Authentication.route, navController = navController)
            }
        }
    }
}
