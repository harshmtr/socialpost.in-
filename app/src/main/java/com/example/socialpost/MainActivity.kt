package com.example.socialpost

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.socialpost.ui.SocialPostApp
import com.example.socialpost.ui.theme.SocialPostTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SocialPostTheme {
                SocialPostApp()
            }
        }
    }
}
