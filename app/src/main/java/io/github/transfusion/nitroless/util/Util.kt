package io.github.transfusion.nitroless.util

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlin.math.roundToInt

suspend fun <A, B> Iterable<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    map { async { f(it) } }.awaitAll()
}


fun convertDpToPx(dp: Int): Int {
    val metrics = Resources.getSystem().displayMetrics
    return dp * (metrics.densityDpi / 160f).roundToInt()
}

fun convertPxToDp(px: Int): Int {
    val metrics = Resources.getSystem().displayMetrics
    return (px / (metrics.densityDpi / 160f)).roundToInt()
}

// https://gist.github.com/chrisbanes/bcf4b11154cb59e3f302f278902eb3f7
fun createNightModeContext(context: Context, isNightMode: Boolean): Context {
    val uiModeFlag =
        if (isNightMode) Configuration.UI_MODE_NIGHT_YES else Configuration.UI_MODE_NIGHT_NO
    val config = Configuration(context.resources.configuration)
    config.uiMode = uiModeFlag or (config.uiMode and Configuration.UI_MODE_NIGHT_MASK.inv())
    return context.createConfigurationContext(config)
}