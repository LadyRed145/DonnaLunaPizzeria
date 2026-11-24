package com.donnaluna.pizzeriaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.donnaluna.pizzeriaapp.ui.PantallaDonnaLunaPremium

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val colores = if (isSystemInDarkTheme()) {
                darkColorScheme(
                    primary = Color(0xFFE7C8FF),
                    onPrimary = Color(0xFF2C123E),
                    secondary = Color(0xFFF1B27A),
                    onSecondary = Color(0xFF44210A),
                    tertiary = Color(0xFFFFE08C),
                    background = Color(0xFF17111D),
                    surface = Color(0xFF241B2D),
                    surfaceVariant = Color(0xFF32253F),
                    onSurface = Color(0xFFF6EEF8),
                    onSurfaceVariant = Color(0xFFD8C7E2)
                )
            } else {
                lightColorScheme(
                    primary = Color(0xFF6D4593),
                    onPrimary = Color.White,
                    secondary = Color(0xFFE8A46C),
                    onSecondary = Color(0xFF4B250A),
                    tertiary = Color(0xFFF9D56E),
                    background = Color(0xFFF8F3FA),
                    surface = Color(0xFFFFFCFF),
                    surfaceVariant = Color(0xFFF0E8F5),
                    onSurface = Color(0xFF261D2D),
                    onSurfaceVariant = Color(0xFF6D6177)
                )
            }
            MaterialTheme(colorScheme = colores) {
                PantallaDonnaLunaPremium()
            }
        }
    }
}
