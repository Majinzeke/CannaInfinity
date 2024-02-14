package com.mz.cannainfinity.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mz.cannainfinity.presentation.screens.auth.AuthenticationScreen
import com.mz.cannainfinity.presentation.screens.auth.AuthenticationViewModel
import com.mz.cannainfinity.presentation.screens.home.HomeScreen
import com.mz.cannainfinity.util.Constants.WRITE_SCREEN_ARGUMENT_KEY
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import java.lang.Exception

@Composable
fun SetupNavGraph(startDestination: String, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authenticationRoute()
        homeRoute(navigateToWrite = {
            navController.navigate(Screen.Write.route)
        })
        writeRoute()
    }
}

fun NavGraphBuilder.authenticationRoute() {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()
        AuthenticationScreen(
            authenticated = viewModel.authenticated.value,
            loadingState = false,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onTokenIdReceived = {tokenId ->
                viewModel.signInWithMongoAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                                    messageBarState.addSuccess("Success")

                    },
                    onError = {
                        messageBarState.addError(it)

                    }
                )
            },
            onDialogDismissed = {

            }
        ) {

        }
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit
) {
    composable(route = Screen.Home.route) {
        HomeScreen(
            onMenuClicked = { /*TODO*/ },
            navigateToWrite = navigateToWrite
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