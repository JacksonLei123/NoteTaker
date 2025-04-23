package com.app.notetaker.ui.textEditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun RichTextToolbar(
    modifier: Modifier = Modifier,
    isBold: Boolean,
    isItalic: Boolean,
    onBoldToggle: () -> Unit = {},
    onItalicToggle: () -> Unit = {},
    onUnderlineToggle: () -> Unit = {},
    onBulletToggle: () -> Unit = {}
) {
    var boldState by remember { mutableStateOf(isBold) }
    var italicState by remember { mutableStateOf(isItalic) }
    Row(
        modifier = modifier.then(
            other = Modifier
                .fillMaxWidth()
                .background(color = Color.LightGray),
        ),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = {boldState = !boldState; onBoldToggle()}) {
            Icon(
                imageVector = Icons.Default.FormatBold,
                contentDescription = "Bold",
                tint = if (boldState) Color.Blue else Color.Black
            )
        }
        IconButton(onClick = onItalicToggle) {
            Icon(
                imageVector = Icons.Default.FormatItalic,
                contentDescription = "Italic",
                tint = if (isItalic) Color.Blue else Color.Black
            )
        }
        IconButton(onClick = onUnderlineToggle) {
            Icon(
                imageVector = Icons.Default.FormatUnderlined,
                contentDescription = "Underlined",
                tint = if (isItalic) Color.Blue else Color.Black
            )
        }
        IconButton(onClick = onBulletToggle) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.FormatListBulleted,
                contentDescription = "Bulleted",
            )
        }
    }
}
