package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.AuraBotApp
import com.example.ui.AuraBotViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AuraBotViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraBotApp(viewModel = viewModel)
        }
    }
}
