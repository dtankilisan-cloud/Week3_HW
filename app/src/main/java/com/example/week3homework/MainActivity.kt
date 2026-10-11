package com.example.week3homework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.example.week3homework.Soal1.ReactionTestScreen
import com.example.week3homework.Soal1Bonus.CoffeeOrderScreen
import com.example.week3homework.Soal2.CatClickerScreen
import com.example.week3homework.Soal2Bonus.TravelJournalScreen
import com.example.week3homework.Soal3.ColorWordMatchingScreen
import com.example.week3homework.Soal4.RockPaperScissorsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // ReactionTestScreen()
                // CatClickerScreen()
                // ColorWordMatchingScreen()
                // RockPaperScissorsScreen()
                // CoffeeOrderScreen()
                //TravelJournalScreen()
            }
        }
    }
}