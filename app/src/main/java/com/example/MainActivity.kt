package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.PreferencesRepository
import com.example.data.local.TractorDatabase
import com.example.data.repository.TractorRepository
import com.example.ui.TractorNondApp
import com.example.ui.theme.TractorNondTheme
import com.example.ui.viewmodel.TractorViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = TractorDatabase.getInstance(applicationContext)
        val preferences = PreferencesRepository(applicationContext)
        val repository = TractorRepository(database.tractorDao(), preferences)
        val viewModelFactory = TractorViewModel.Factory(repository)

        setContent {
            TractorNondTheme {
                val tractorViewModel: TractorViewModel = viewModel(factory = viewModelFactory)
                TractorNondApp(viewModel = tractorViewModel)
            }
        }
    }
}
