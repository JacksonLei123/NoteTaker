package com.app.notetaker.ui.textEditor

import android.content.Context.WINDOW_SERVICE
import android.content.res.Resources
import android.graphics.Insets
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RichTextField(state: TextFieldState, modifier: Modifier = Modifier, topInset: Dp = 0.dp) {
    val textFieldState = remember { state }
    val scrollState = rememberScrollState()
    var cursorCoordinates by remember {mutableStateOf<LayoutCoordinates?>(null) }
    var keyboardHeight: Int
    val view = LocalView.current

    val windowManager = LocalContext.current.getSystemService(WINDOW_SERVICE) as WindowManager
    val displayMetrics = Resources.getSystem().displayMetrics
    val screenHeight: Int
    val topSystemBarHeight: Int
    val topSpacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topInset.value, displayMetrics).toInt()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsetsCompat.Type.systemBars())
        topSystemBarHeight = insets.top
        screenHeight = windowMetrics.bounds.height() - insets.top - topSpacing
    } else {
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        topSystemBarHeight = WindowInsets.systemBars.getTop(LocalDensity.current)
        screenHeight = displayMetrics.heightPixels - topSystemBarHeight - topSpacing
    }
    val coroutineScope = rememberCoroutineScope()
    BasicTextField(textFieldState, textStyle = TextStyle(fontSize = 20.sp), modifier = modifier.then(
        other = Modifier
            .verticalScroll(scrollState)
            .onGloballyPositioned { layoutCoordinates ->
                cursorCoordinates = layoutCoordinates
            }
    ),
        onTextLayout = {textLayoutResult ->
            val textLayout = textLayoutResult()
            val cursorRect = textLayout?.getCursorRect(textFieldState.selection.start)
            coroutineScope.launch {
                delay(100)
                val insets = ViewCompat.getRootWindowInsets(view)
                keyboardHeight = insets!!.getInsets(WindowInsetsCompat.Type.ime()).bottom
                val coordinates = cursorCoordinates!!.localToWindow(cursorRect!!.bottomLeft)
                if (coordinates.y - topSystemBarHeight > screenHeight -  keyboardHeight && keyboardHeight > 0) {
                    scrollState.animateScrollTo((cursorRect.bottom - (screenHeight - keyboardHeight)).toInt())
                }
            }
        }
    )
}
