package eu.kanade.tachiyomi.ui.browse.extension.details

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.ExtensionDetailsScreen
import kotlinx.coroutines.flow.collectLatest
import tachiyomi.presentation.core.components.LoadingScreen

data class ExtensionDetailsScreen(
    private val pkgName: String,
) : Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val screenModel = rememberScreenModel { ExtensionDetailsScreenModel(pkgName = pkgName, context = context) }
        val state by screenModel.state.collectAsState()

        if (state.isLoading) {
            LoadingScreen()
            return
        }

        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current

        ExtensionDetailsScreen(
            navigateUp = navigator::pop,
            state = state,
            onClickSourcePreferences = { navigator.push(SourcePreferencesScreen(it)) },
            onClickWhatsNew = { uriHandler.openUri(screenModel.getChangelogUrl()) },
            onClickReadme = { uriHandler.openUri(screenModel.getReadmeUrl()) },
            onClickEnableAll = { screenModel.toggleSources(true) },
            onClickDisableAll = { screenModel.toggleSources(false) },
            onClickClearCookies = { screenModel.clearCookies() },
            onClickUninstall = { screenModel.uninstallExtension() },
            onClickSource = { screenModel.toggleSource(it) },
        )

        LaunchedEffect(Unit) {
            screenModel.events.collectLatest { event ->
                if (event is ExtensionDetailsEvent.Uninstalled) {
                    navigator.pop()
                }
            }
        }
    }
}
