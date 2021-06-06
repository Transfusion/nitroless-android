package io.github.transfusion.nitroless.util

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
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
