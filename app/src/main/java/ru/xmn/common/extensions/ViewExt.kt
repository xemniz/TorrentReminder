package ru.xmn.common.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.pairSharedTransition(): android.support.v4.util.Pair<View, String> {
    return android.support.v4.util.Pair<View, String>(this, ViewCompat.getTransitionName(this))
}

fun View.isInBounds(x: Int, y: Int): Boolean {
    val outRect = Rect()
    val location = IntArray(2)
    this.getDrawingRect(outRect)
    this.getLocationOnScreen(location)
    outRect.offset(location[0], location[1])
    return outRect.contains(x, y)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}
fun View.invisible() {
    this.visibility = View.INVISIBLE
}
fun View.gone() {
    this.visibility = View.GONE
}
fun List<View>.visibleOnly(vararg view: View) {
    this.forEach {v ->  when{
        view.any { it.id == v.id } -> v.visible()
        else -> v.invisible()
    } }
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun View.changeWidth(w: Int){
    val layoutParams = this.layoutParams
    layoutParams.width = w
    this.layoutParams = layoutParams
}

fun View.changeHeight(w: Int){
    val layoutParams = this.layoutParams
    layoutParams.height = w
    this.layoutParams = layoutParams
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, 0)
}