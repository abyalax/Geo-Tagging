package com.app.core.navigation

sealed class NavigationEvent {
    data object NavigateToLogin : NavigationEvent()
    data object NavigateToDashboard : NavigationEvent()
    data object NavigateToProfile : NavigationEvent()
    data object NavigateBack : NavigationEvent()
    
    data class NavigateToVerification(
        val surveyId: String,
        val locationName: String
    ) : NavigationEvent()
    
    data class NavigateWithPopUp(
        val route: String,
        val popUpTo: String,
        val inclusive: Boolean = false
    ) : NavigationEvent()
    
    data object ClearBackStackAndNavigate : NavigationEvent()
}
