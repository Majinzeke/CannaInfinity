package com.mz.cannainfinity.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mz.cannainfinity.data.MongoDB
import com.mz.cannainfinity.presentation.components.DisplayAlertDialog
import com.mz.cannainfinity.presentation.screens.auth.AuthenticationScreen
import com.mz.cannainfinity.presentation.screens.auth.AuthenticationViewModel
import com.mz.cannainfinity.presentation.screens.home.HomeScreen
import com.mz.cannainfinity.presentation.screens.home.HomeViewModel
import com.mz.cannainfinity.util.Constants.APP_ID
import com.mz.cannainfinity.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Authentication.route
    ) {
        authenticationRoute(
            navigateToHome = {
                navController.navigate(Screen.Home.route)
            }
        )
        homeRoute(
            navigateToWrite = {
            navController.navigate(Screen.Write.route)
        },
            navigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            }
        )
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute(navigateToHome: () -> Unit) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState
        val authenticated by viewModel.authenticated
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = false,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onTokenIdReceived = { tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Success")
                        viewModel.setLoading(false)
                    },
                    onError = {
                        messageBarState.addError(it)

                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.homeRoute(
    navigateToAuth: () -> Unit,
    navigateToWrite: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = hiltViewModel()
        val entries by viewModel.entries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var signOutDialogOpened by remember { mutableStateOf(false) }

        HomeScreen(
            entries = entries,
            drawerState = drawerState,
            onMenuClicked = {
                scope.launch {
                    drawerState.open()
                    navigateToWrite()
                }
            },
            navigateToWrite = navigateToWrite,
            onSignedOutClicked = {
                signOutDialogOpened = true
            }

        )
        LaunchedEffect(key1 = Unit) {
            MongoDB.configureTheRealm()
        }

        DisplayAlertDialog(
            title = "Sign out",
            message = "Are you sure you want to sign out?",
            dialogOpened = signOutDialogOpened,
            onDialogClosed = { signOutDialogOpened = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(APP_ID).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            navigateToAuth()
                        }
                    }
                }
            }
        )


    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = WRITE_SCREEN_ARGUMENT_KEY) {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })
    ) {

    }
}