package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.PrayerRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import com.example.util.CompassManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup SQLite Persistence Infrastructure
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = PrayerRepository(db.prayerDao())
        
        // Initialise State ViewModel
        val viewModel: MainViewModel by viewModels {
            MainViewModel.Factory(repository)
        }
        
        // Setup Sensor Magnet Compass hardware listener
        val compassManager = CompassManager(applicationContext)
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainScreen(
                    viewModel = viewModel,
                    compassManager = compassManager,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

