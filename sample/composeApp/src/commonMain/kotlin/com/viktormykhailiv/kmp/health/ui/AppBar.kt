package com.viktormykhailiv.kmp.health.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.viktormykhailiv.kmp.health.navigation.LocalNavController

@Composable
fun AppBar(
    title: String,
    withNavigationButton: Boolean = false,
) {
    val navController = if (withNavigationButton) {
        LocalNavController.current
    } else {
        null
    }

    TopAppBar(
        modifier = Modifier.appGradient(),
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        title = { Text(title, color = Color.White) },
        navigationIcon = navController?.let {
            {
                IconButton(
                    onClick = { navController.popBackStack() },
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            }
        },
    )
}
