package com.example.week3homework.Soal4


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay


private const val REVEAL_DELAY_MS = 700L // lama reveal sebelum auto-lanjut

enum class Rps(val emoji: String, val label: String) {
    ROCK("✊", "Rock"),
    PAPER("✋", "Paper"),
    SCISSORS("✌️", "Scissor");

    fun beats(other: Rps): Boolean = when (this) {
        ROCK -> other == SCISSORS
        PAPER -> other == ROCK
        SCISSORS -> other == PAPER
    }
}

enum class State { INITIAL, PICK, REVEAL, FINISHED }

enum class Result(val text: String) {
    WIN("You Win!"),
    LOSE("You Lose!"),
    DRAW("Draw")
}

@Composable
fun RockPaperScissorsScreen(modifier: Modifier = Modifier, bestOf: Int = 5) {
    require(bestOf % 2 == 1) { "bestOf harus ganjil (3/5/7)" }
    val targetScore = bestOf / 2 + 1 // contoh: Best of 5 -> first to 3

    var state by rememberSaveable { mutableStateOf(State.INITIAL) }
    var yourScore by rememberSaveable { mutableIntStateOf(0) }
    var cpuScore by rememberSaveable { mutableIntStateOf(0) }
    var bestScore by rememberSaveable { mutableIntStateOf(0) }

    var playerPick by rememberSaveable { mutableStateOf<Rps?>(null) }
    var cpuPick by rememberSaveable { mutableStateOf<Rps?>(null) }
    var result by rememberSaveable { mutableStateOf<Result?>(null) }
    var buttonOrder by remember { mutableStateOf(Rps.values().toList().shuffled()) }

    fun resetRound() {
        playerPick = null
        cpuPick = null
        result = null
        buttonOrder = Rps.values().toList().shuffled()
    }

    fun startMatch() {
        yourScore = 0
        cpuScore = 0
        resetRound()
        state = State.PICK
    }

    fun onPick(pick: Rps) {
        if (state != State.PICK) return
        val cpu = Rps.values().random()
        val roundResult = when {
            pick == cpu -> Result.DRAW
            pick.beats(cpu) -> Result.WIN
            else -> Result.LOSE
        }
        playerPick = pick
        cpuPick = cpu
        result = roundResult
        when (roundResult) {
            Result.WIN -> yourScore++
            Result.LOSE -> cpuScore++
            Result.DRAW -> Unit
        }
        state = State.REVEAL
    }

    LaunchedEffect(state) {
        if (state == State.REVEAL) {
            delay(REVEAL_DELAY_MS)
            if (yourScore >= targetScore || cpuScore >= targetScore) {
                val diff = yourScore - cpuScore
                if (diff > bestScore) bestScore = diff
                state = State.FINISHED
            } else {
                resetRound()
                state = State.PICK
            }
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
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ScoreHeader(yourScore = yourScore, cpuScore = cpuScore, bestOf = bestOf)
            Spacer(Modifier.height(32.dp))

            when (state) {
                State.INITIAL -> {
                    Text(
                        text = "Rock • Paper • Scissors",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        color = Color(0xFF212121)
                    )
                    Spacer(Modifier.height(24.dp))
                    RpsButton(text = "Start", onClick = { startMatch() })
                }

                State.PICK -> {
                    Text("Pick your move!", fontSize = 14.sp, color = Color(0xFF424242))
                    Spacer(Modifier.height(12.dp))
                    VersusRow(left = "❔", right = "❔")
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        buttonOrder.forEach { rps ->
                            Button(
                                onClick = { onPick(rps) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(64.dp)
                                    .semantics { contentDescription = "Pilih ${rps.label}" },
                                colors = rpsButtonColors()
                            ) {
                                Text("${rps.emoji} ${rps.label}", fontSize = 13.sp)
                            }
                        }
                    }
                }

                State.REVEAL -> {
                    val player = playerPick
                    val cpu = cpuPick
                    val res = result
                    if (player != null && cpu != null && res != null) {
                        VersusRow(
                            left = player.emoji,
                            right = cpu.emoji,
                            description = "Kamu memilih ${player.label}, komputer memilih ${cpu.label}"
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = res.text,
                            fontSize = 16.sp,
                            color = Color(0xFF424242),
                            modifier = Modifier.semantics { contentDescription = "Hasil ronde: ${res.text}" }
                        )
                    }
                }

                State.FINISHED -> {
                    val won = yourScore > cpuScore
                    val message = if (won) "You Win the Match!" else "You Lose the Match"
                    Text(
                        text = message,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Light,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF212121),
                        modifier = Modifier.semantics { contentDescription = "Hasil match: $message" }
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Best Score: $bestScore", fontSize = 12.sp, color = Color(0xFF424242))
                    Spacer(Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        RpsButton(text = "Restart", onClick = { startMatch() })
                        RpsButton(
                            text = "Exit",
                            onClick = {
                                yourScore = 0
                                cpuScore = 0
                                resetRound()
                                state = State.INITIAL
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreHeader(yourScore: Int, cpuScore: Int, bestOf: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🧑 $yourScore — $cpuScore 🤖",
            fontSize = 14.sp,
            color = Color(0xFF424242),
            modifier = Modifier.semantics {
                contentDescription = "Skor kamu $yourScore, skor komputer $cpuScore"
            }
        )
        Text("Best of $bestOf", fontSize = 12.sp, color = Color(0xFF424242))
    }
}

@Composable
private fun VersusRow(left: String, right: String, description: String? = null) {
    Text(
        text = "$left VS $right",
        fontSize = 34.sp,
        fontWeight = FontWeight.Light,
        color = Color(0xFF212121),
        modifier = if (description != null) {
            Modifier.semantics { contentDescription = description }
        } else {
            Modifier
        }
    )
}

@Composable
private fun RpsButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, colors = rpsButtonColors()) {
        Text(text = text, fontSize = 14.sp)
    }
}

@Composable
private fun rpsButtonColors() = ButtonDefaults.buttonColors(
    containerColor = Color(0xFFB0BEC5),
    contentColor = Color(0xFF263238)
)