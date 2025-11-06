package com.paradox543.malankaraorthodoxliturgica.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun RestoreTimePickerPreview() {
    RestoreTimePicker({ minutes -> }, {}, 30)
}