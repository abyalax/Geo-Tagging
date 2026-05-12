package com.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.app.core.data.repository.AuthRepository
import com.app.core.data.repository.AuthRepositoryImpl
import com.app.core.domain.usecase.auth.GetSessionUseCase
import com.app.core.domain.usecase.auth.LogoutUseCase
import com.app.core.navigation.AndroidNavigationManager
import com.app.core.navigation.DefaultNavigationEventEmitter
import com.app.core.theme.ApplicationTheme
import com.app.features.auth.login.presentation.viewmodel.LoginViewModel
import com.app.features.dashboard.domain.usecase.GetSurveysUseCase
import com.app.features.dashboard.domain.usecase.OpenSurveyMapUseCase
import com.app.features.dashboard.domain.usecase.ShareSurveyUseCase
import com.app.features.dashboard.viewmodel.DashboardViewModel
import com.app.navigation.AppNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ApplicationTheme {
                val navigationEventEmitter = remember { DefaultNavigationEventEmitter() }
                val androidNavigationManager =
                        remember(navigationEventEmitter) {
                            AndroidNavigationManager(navigationEventEmitter)
                        }
                val authRepository: AuthRepository =
                        remember { AuthRepositoryImpl(this@MainActivity) }
                val getSessionUseCase = remember { GetSessionUseCase(authRepository) }
                val loginUseCase =
                        remember { com.app.core.domain.usecase.auth.LoginUseCase(authRepository) }
                val logoutUseCase = remember { LogoutUseCase(authRepository) }
                val getSurveysUseCase = remember { GetSurveysUseCase() }
                val shareSurveyUseCase = remember { ShareSurveyUseCase() }
                val openSurveyMapUseCase = remember { OpenSurveyMapUseCase() }

                val loginViewModel =
                        remember(loginUseCase, androidNavigationManager) {
                            LoginViewModel(
                                    loginUseCase = loginUseCase,
                                    navigationManager = androidNavigationManager,
                                    context = this@MainActivity
                            )
                        }

                val dashboardViewModel =
                        remember(
                                getSurveysUseCase,
                                getSessionUseCase,
                                logoutUseCase,
                                shareSurveyUseCase,
                                openSurveyMapUseCase
                        ) {
                            DashboardViewModel(
                                    getSurveysUseCase = getSurveysUseCase,
                                    getSessionUseCase = getSessionUseCase,
                                    logoutUseCase = logoutUseCase,
                                    shareSurveyUseCase = shareSurveyUseCase,
                                    openSurveyMapUseCase = openSurveyMapUseCase
                            )
                        }

                AppNavHost(
                        navigationEventEmitter = navigationEventEmitter,
                        androidNavigationManager = androidNavigationManager,
                        getSessionUseCase = getSessionUseCase,
                        loginViewModel = loginViewModel,
                        dashboardViewModel = dashboardViewModel
                )
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {}
