sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Dashboard : Routes("dashboard")
    data object Profile : Routes("profile")
    data object Verification : Routes("verification/{surveyId}/{locationName}") {
        fun createRoute(surveyId: String, locationName: String): String {
            return "verification/$surveyId/$locationName"
        }
    }
}
