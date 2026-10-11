package com.example.week3homework.Soal1 // TODO: sesuaikan dengan nama package project Anda

import android.os.SystemClock
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week3homework.R // TODO: sesuaikan dengan nama package project Anda
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

// =====================================================================
// Konstanta
// =====================================================================

private const val MIN_DELAY_MS = 500L   // delay acak minimum (ms)
private const val MAX_DELAY_MS = 4500L  // delay acak maksimum (ms)
private const val TOTAL_TRIALS = 3      // jumlah trial

// =====================================================================
// Enum
// =====================================================================

/** Tahapan layar pada game Reaction Test. */
enum class ReactionState {
    START,           // tampilan awal
    WAITING,         // tampilan menunggu (abu-abu)
    READY,           // tampilan GO! (hijau) -> saatnya menekan
    TRIAL_COMPLETE,  // trial berhasil (1 & 2)
    FAIL,            // menekan terlalu cepat
    FINAL            // hasil akhir
}

/** Kategori hasil berdasarkan rata-rata waktu reaksi (ms). */
enum class ReactionCategory(
    val message: String,
    val imageRes: Int,
    val imageDescription: String,
    val color: Color
) {
    FAST("DANG YOU ARE SO FAST BRO!", R.drawable.reaction_fast, "Foto orang hormat", Color(0xFF00E676)),
    GOOD("YOUR REFLEX IS GOOD", R.drawable.reaction_good, "Foto orang jempol", Color(0xFF2196F3)),
    MEH("MEH LIKE OTHER PERSON", R.drawable.reaction_meh, "Foto orang standard", Color(0xFFFF9800)),
    SNAIL("YOU LIKE A SNAIL BRO", R.drawable.reaction_snail, "Foto jempol ke bawah dan muka menghina", Color(0xFFFF5722))
}

private fun categoryOf(average: Int?): ReactionCategory = when {
    average == null -> ReactionCategory.SNAIL
    average < 180 -> ReactionCategory.FAST
    average < 280 -> ReactionCategory.GOOD
    average < 450 -> ReactionCategory.MEH
    else -> ReactionCategory.SNAIL
}

@Composable
fun ReactionTestScreen(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(ReactionState.START) }
    var trial by remember { mutableIntStateOf(0) }                       // 0, 1, 2
    val results = remember { mutableStateListOf<Int?>(null, null, null) } // null = gagal / belum
    var readyAt by remember { mutableLongStateOf(0L) }                   // waktu layar berubah hijau

    LaunchedEffect(state, trial) {
        if (state == ReactionState.WAITING) {
            delay(Random.nextLong(MIN_DELAY_MS, MAX_DELAY_MS + 1))
            readyAt = SystemClock.elapsedRealtime()
            state = ReactionState.READY
        }
    }

    val isLastTrial = trial == TOTAL_TRIALS - 1
    val validResults = results.filterNotNull()
    val average: Int? = if (validResults.isEmpty()) null else validResults.average().roundToInt()
    val category = categoryOf(average)

    fun onScreenTap() {
        when (state) {
            ReactionState.START -> state = ReactionState.WAITING

            ReactionState.WAITING -> state = ReactionState.FAIL

            ReactionState.READY -> {
                results[trial] = (SystemClock.elapsedRealtime() - readyAt).toInt()
                state = if (isLastTrial) ReactionState.FINAL else ReactionState.TRIAL_COMPLETE
            }

            ReactionState.TRIAL_COMPLETE -> {
                trial++
                state = ReactionState.WAITING
            }

            ReactionState.FAIL -> {
                if (isLastTrial) {
                    state = ReactionState.FINAL
                } else {
                    trial++
                    state = ReactionState.START
                }
            }

            ReactionState.FINAL -> {
                for (i in results.indices) results[i] = null
                trial = 0
                state = ReactionState.START
            }
        }
    }

    val backgroundColor = when (state) {
        ReactionState.START -> Color(0xFF6FCDDF)
        ReactionState.WAITING -> Color(0xFFD6D6D6)
        ReactionState.READY, ReactionState.TRIAL_COMPLETE -> Color(0xFF4CAF50)
        ReactionState.FAIL -> Color(0xFFFA4343)
        ReactionState.FINAL -> category.color
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onScreenTap() }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            ReactionState.START -> ReactionContent(
                title = "Reaction",
                icon = { BoltIcon() },
                subtitle = "Test",
                hint = "Click to Start",
                results = if (trial > 0) results else null
            )

            ReactionState.WAITING -> ReactionContent(
                title = "Get Ready",
                icon = { WarningIcon(cutColor = backgroundColor) },
                subtitle = "Wait for green light...",
                hint = "DON'T CLICK YET!"
            )

            ReactionState.READY -> ReactionContent(
                title = "GO!",
                icon = { Text("🏃", fontSize = 84.sp) },
                subtitle = "CLICK NOW!",
                hint = "TAP AS FAST AS YOU CAN!"
            )

            ReactionState.TRIAL_COMPLETE -> ReactionContent(
                title = "Trial ${trial + 1} Complete!",
                icon = { CheckIcon() },
                subtitle = "Time: ${results[trial]}ms",
                hint = "Continue to Trial ${trial + 2}",
                results = results
            )

            ReactionState.FAIL -> ReactionContent(
                title = "FAIL!",
                icon = { Text("👎", fontSize = 84.sp) },
                subtitle = "You clicked too early, TRY TO READ THE RULE BRO",
                hint = if (isLastTrial) "SEE RESULT" else "TRY AGAIN",
                results = results
            )

            ReactionState.FINAL -> FinalContent(
                category = category,
                average = average,
                results = results
            )
        }
    }
}

@Composable
private fun ReactionContent(
    title: String,
    icon: @Composable () -> Unit,
    subtitle: String,
    hint: String,
    results: List<Int?>? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))
        icon()
        Spacer(Modifier.height(36.dp))
        Text(
            text = subtitle,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = hint,
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
        if (results != null) {
            Spacer(Modifier.height(28.dp))
            TrialResultsCard(results)
        }
    }
}

@Composable
private fun FinalContent(
    category: ReactionCategory,
    average: Int?,
    results: List<Int?>
) {
    val averageText = if (average != null) "${average}ms" else "-"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = category.message,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Image(
            painter = painterResource(id = category.imageRes),
            contentDescription = category.imageDescription,
            modifier = Modifier.size(170.dp)
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Average: $averageText",
            color = Color.White,
            fontSize = 16.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Click to Start New Test",
            color = Color.White,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(28.dp))
        TrialResultsCard(results = results, averageText = averageText)
    }
}

@Composable
private fun TrialResultsCard(results: List<Int?>, averageText: String? = null) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.92f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Trial Results",
                color = Color(0xFF1976D2),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                results.forEachIndexed { index, value ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${index + 1}",
                            color = if (value != null) Color(0xFF2E7D32) else Color.Gray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (value != null) "${value}ms" else "-",
                            color = Color.Black,
                            fontSize = 11.sp
                        )
                    }
                }
            }
            if (averageText != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Average Score",
                    color = Color(0xFF1976D2),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = averageText,
                    color = Color(0xFFE65100),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}


@Composable
private fun BoltIcon(iconSize: Dp = 96.dp, color: Color = Color.White) {
    Canvas(modifier = Modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val bolt = Path().apply {
            moveTo(w * 0.62f, 0f)
            lineTo(w * 0.18f, h * 0.56f)
            lineTo(w * 0.46f, h * 0.56f)
            lineTo(w * 0.34f, h)
            lineTo(w * 0.82f, h * 0.38f)
            lineTo(w * 0.54f, h * 0.38f)
            close()
        }
        drawPath(bolt, color)
    }
}

@Composable
private fun WarningIcon(cutColor: Color, iconSize: Dp = 96.dp, color: Color = Color.White) {
    Canvas(modifier = Modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        val triangle = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.96f, h * 0.92f)
            lineTo(w * 0.04f, h * 0.92f)
            close()
        }
        drawPath(triangle, color)
        drawRoundRect(
            color = cutColor,
            topLeft = Offset(w * 0.45f, h * 0.36f),
            size = Size(w * 0.10f, h * 0.28f),
            cornerRadius = CornerRadius(4f, 4f)
        )
        drawCircle(cutColor, radius = w * 0.06f, center = Offset(w * 0.5f, h * 0.76f))
    }
}

@Composable
private fun CheckIcon(iconSize: Dp = 96.dp) {
    Canvas(modifier = Modifier.size(iconSize)) {
        val w = size.width
        val h = size.height
        drawCircle(Color.White)
        val check = Path().apply {
            moveTo(w * 0.27f, h * 0.52f)
            lineTo(w * 0.43f, h * 0.68f)
            lineTo(w * 0.74f, h * 0.34f)
        }
        drawPath(
            path = check,
            color = Color(0xFF4CAF50),
            style = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}