package com.example.week3homework.Soal2

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.week3homework.R
import kotlinx.coroutines.delay
import kotlin.math.roundToLong


private const val INITIAL_VALUE = 1L
private const val INITIAL_COST = 10L
private const val COST_MULTIPLIER = 2L
private const val VALUE_MULTIPLIER = 1.5
private const val MOUTH_CLOSE_DELAY_MS = 150L
private fun nextValue(current: Long): Long = (current * VALUE_MULTIPLIER).roundToLong()

private val ReadableShadow = TextStyle(
    shadow = Shadow(color = Color.Black.copy(alpha = 0.7f), offset = Offset(2f, 2f), blurRadius = 6f)
)


@Composable
fun CatClickerScreen(modifier: Modifier = Modifier) {
    var coins by rememberSaveable { mutableLongStateOf(0L) }
    var coinsPerTap by rememberSaveable { mutableLongStateOf(INITIAL_VALUE) }
    var upgradeCost by rememberSaveable { mutableLongStateOf(INITIAL_COST) }

    var isHolding by remember { mutableStateOf(false) }
    var tapCount by remember { mutableIntStateOf(0) }
    var mouthOpen by remember { mutableStateOf(false) }

    LaunchedEffect(isHolding, tapCount) {
        if (!isHolding) {
            delay(MOUTH_CLOSE_DELAY_MS)
            mouthOpen = false
        }
    }

    val canUpgrade = coins >= upgradeCost

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.cat_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CoinCounter(coins = coins, coinsPerTap = coinsPerTap)

            Spacer(Modifier.weight(1f))

            CatArea(
                mouthOpen = mouthOpen,
                onPress = {
                    coins += coinsPerTap
                    isHolding = true
                    mouthOpen = true
                    tapCount++
                },
                onRelease = { isHolding = false }
            )

            Spacer(Modifier.weight(1f))

            UpgradeCard(
                nextValue = nextValue(coinsPerTap),
                cost = upgradeCost,
                coins = coins,
                enabled = canUpgrade,
                onUpgrade = {
                    if (coins >= upgradeCost) {
                        coins -= upgradeCost
                        upgradeCost *= COST_MULTIPLIER
                        coinsPerTap = nextValue(coinsPerTap)
                    }
                }
            )
        }
    }
}


@Composable
private fun CoinCounter(coins: Long, coinsPerTap: Long) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 36.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Coins",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$coins",
            color = Color(0xFF00E676),
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "$coinsPerTap coins per tap",
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun CatArea(
    mouthOpen: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    val currentOnPress by rememberUpdatedState(onPress)
    val currentOnRelease by rememberUpdatedState(onRelease)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Tap the Cat!",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            style = ReadableShadow
        )
        Spacer(Modifier.height(12.dp))

        Image(
            painter = painterResource(
                id = if (mouthOpen) R.drawable.cat_open else R.drawable.cat_closed
            ),
            contentDescription = if (mouthOpen) "Kucing mulut terbuka" else "Kucing mulut tertutup",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(220.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = {
                            currentOnPress()
                            tryAwaitRelease()
                            currentOnRelease()
                        }
                    )
                }
                .scale(if (mouthOpen) 0.94f else 1f)
                .clip(RoundedCornerShape(20.dp))
        )

        Spacer(Modifier.height(8.dp))
        Text(
            text = if (mouthOpen) "Meow!" else "Purr~",
            color = Color.White,
            fontSize = 16.sp,
            style = ReadableShadow
        )
    }
}

@Composable
private fun UpgradeCard(
    nextValue: Long,
    cost: Long,
    coins: Long,
    enabled: Boolean,
    onUpgrade: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.92f))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Give Me Your Coin",
            color = Color(0xFF424242),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Next upgrade: +$nextValue coins per tap",
            color = Color(0xFF757575),
            fontSize = 14.sp
        )
        Spacer(Modifier.height(14.dp))
        Button(
            onClick = onUpgrade,
            enabled = enabled,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFBDBDBD),
                disabledContentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (enabled) "Pay for $cost coins" else "Find ${cost - coins} more coins",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}