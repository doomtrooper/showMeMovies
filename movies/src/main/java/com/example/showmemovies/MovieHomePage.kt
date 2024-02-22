package com.example.showmemovies

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "Text preview")
@Composable
fun Profile() {
    Box(modifier = Modifier.fillMaxWidth().fillMaxHeight().background(Color.Red)){
        Text( text = "Profile")
    }
}