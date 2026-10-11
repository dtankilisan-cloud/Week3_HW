package com.example.week3homework.Soal3

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val TIME_LIMIT_MS = 5000L
private const val TICK_MS = 50L
private const val COUNTDOWN_STEP_MS = 1000L
private const val START_TEXT_MS = 700L
private const val MAX_STRIKES = 3

enum class GameState { INITIAL, COUNTDOWN, RUNNING, GAME_OVER }

enum class Mode(val label: String, val clue: String) {
    COLOR("COLOR", "Pilih WARNA TINTA"),
    TEXT("TEXT", "Pilih NAMA KATA")
}

enum class Choice { INK, WORD }

enum class ColorName(val color: Color) {
    RED(Color(0xFFE53935)),
    BLUE(Color(0xFF1E88E5)),
    GREEN(Color(0xFF43A047)),
    ORANGE(Color(0xFFFB8C00)),
    PURPLE(Color(0xFF8E24AA))
}


@Composable
fun ColorWordMatchingScreen(modifier: Modifier = Modifier) {
    // ----- State permainan
    var gameState by remember { mutableStateOf(GameState.INITIAL) }
    var countdownText by remember { mutableStateOf("") }
    var score by remember { mutableIntStateOf(0) }
    var strikes by remember { mutableIntStateOf(0) }
    var bestScore by rememberSaveable { mutableIntStateOf(0) }
    var mode by remember { mutableStateOf(Mode.COLOR) }
    var word by remember { mutableStateOf(ColorName.RED) }
    var ink by remember { mutableStateOf(ColorName.BLUE) }
    var inkOnLeft by remember { mutableStateOf(true) }
    var remainingMs by remember { mutableLongStateOf(TIME_LIMIT_MS) }
    var questionId by remember { mutableIntStateOf(0) }

    fun newQuestion() {
        mode = Mode.values().random()
        word = ColorName.values().random()
        ink = ColorName.values().filter { it != word }.random()
        inkOnLeft = Random.nextBoolean()
        remainingMs = TIME_LIMIT_MS
        questionId++
    }

    fun registerWrong() {
        strikes++
        if (strikes >= MAX_STRIKES) {
            if (score > bestScore) bestScore = score
            gameState = GameState.GAME_OVER
        } else {
            newQuestion()
        }
    }

    fun onAnswer(choice: Choice) {
        if (gameState != GameState.RUNNING) return
        val correct = when (mode) {
            Mode.COLOR -> choice == Choice.INK
            Mode.TEXT -> choice == Choice.WORD
        }
        if (correct) {
            score++
            newQuestion()
        } else {
            registerWrong()
        }
    }

    LaunchedEffect(gameState) {
        if (gameState == GameState.COUNTDOWN) {
            for (n in 3 downTo 1) {
                countdownText = n.toString()
                delay(COUNTDOWN_STEP_MS)
            }
            countdownText = "Start!"
            delay(START_TEXT_MS)
            score = 0
            strikes = 0
            newQuestion()
            gameState = GameState.RUNNING
        }
    }

    LaunchedEffect(questionId, gameState) {
        if (gameState == GameState.RUNNING) {
            val startTime = SystemClock.elapsedRealtime()
            while (true) {
                delay(TICK_MS)
                val left = TIME_LIMIT_MS - (SystemClock.elapsedRealtime() - startTime)
                if (left <= 0) {
                    remainingMs = 0
                    break
                }
                remainingMs = left
            }
            registerWrong()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        when (gameState) {
            GameState.INITIAL -> InitialScreen(onStart = { gameState = GameState.COUNTDOWN })

            GameState.COUNTDOWN -> Text(
                text = countdownText,
                fontSize = 40.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF212121)
            )

            GameState.RUNNING -> RunningScreen(
                mode = mode,
                word = word,
                ink = ink,
                inkOnLeft = inkOnLeft,
                score = score,
                strikes = strikes,
                secondsLeft = ((remainingMs + 999) / 1000).toInt(),
                onAnswer = { onAnswer(it) }
            )

            GameState.GAME_OVER -> GameOverScreen(
                score = score,
                bestScore = bestScore,
                onRestart = { gameState = GameState.COUNTDOWN },
                onExit = { gameState = GameState.INITIAL }
            )
        }
    }
}

@Composable
private fun InitialScreen(onStart: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Welcome\nto\nColor Word Matching",
            fontSize = 24.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            color = Color(0xFF212121)
        )
        Spacer(Modifier.height(32.dp))
        GameButton(text = "Start Game", onClick = onStart)
    }
}

@Composable
private fun RunningScreen(
    mode: Mode,
    word: ColorName,
    ink: ColorName,
    inkOnLeft: Boolean,
    score: Int,
    strikes: Int,
    secondsLeft: Int,
    onAnswer: (Choice) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Mode: ${mode.label}", fontSize = 14.sp, color = Color(0xFF424242))
            Text("✅ $score   ❌ $strikes/$MAX_STRIKES", fontSize = 14.sp, color = Color(0xFF424242))
        }

        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF3CD), RoundedCornerShape(12.dp))
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = mode.clue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5D4037)
            )
        }

        Spacer(Modifier.height(32.dp))

        Text("$secondsLeft s", fontSize = 22.sp, fontWeight = FontWeight.Light, color = Color(0xFF424242))

        Spacer(Modifier.height(8.dp))

        Text(
            text = word.name,
            fontSize = 52.sp,
            fontWeight = FontWeight.Normal,
            color = ink.color
        )

        Spacer(Modifier.height(72.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (inkOnLeft) {
                GameButton(text = ink.name, onClick = { onAnswer(Choice.INK) })
                GameButton(text = word.name, onClick = { onAnswer(Choice.WORD) })
            } else {
                GameButton(text = word.name, onClick = { onAnswer(Choice.WORD) })
                GameButton(text = ink.name, onClick = { onAnswer(Choice.INK) })
            }
        }
    }
}

@Composable
private fun GameOverScreen(
    score: Int,
    bestScore: Int,
    onRestart: () -> Unit,
    onExit: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Game Over!", fontSize = 28.sp, fontWeight = FontWeight.Light, color = Color(0xFF212121))
        Spacer(Modifier.height(24.dp))
        Text("You're Score", fontSize = 16.sp, color = Color(0xFF212121))
        Text("$score", fontSize = 26.sp, fontWeight = FontWeight.Light, color = Color(0xFF212121))
        Spacer(Modifier.height(8.dp))
        Text("Best Score", fontSize = 12.sp, color = Color(0xFF424242))
        Text("$bestScore", fontSize = 16.sp, color = Color(0xFF424242))
        Spacer(Modifier.height(20.dp))
        GameButton(text = "Restart Game", onClick = onRestart)
        Spacer(Modifier.height(8.dp))
        GameButton(text = "Exit", onClick = onExit)
    }
}

@Composable
private fun GameButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFB0BEC5),
            contentColor = Color(0xFF263238)
        )
    ) {
        Text(text = text, fontSize = 14.sp)
    }
}