package com.example.week3homework.Soal2Bonus

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val JournalBackground = Color(0xFF0F1426)
private val JournalCard = Color(0xFF1B2236)
private val JournalBorder = Color(0xFF2E374E)
private val JournalAccent = Color(0xFF5BB8F5)
private val JournalTextPrimary = Color(0xFFF1F4FA)
private val JournalTextSecondary = Color(0xFF9AA5BF)

private val satisfactionLevels = listOf("Biasa", "Seru", "Luar Biasa ★")


@Composable
fun TravelJournalScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // ----- State form -----
    var favoriteSpot by rememberSaveable { mutableStateOf("") }
    var experience by rememberSaveable { mutableStateOf("") }
    var extraNotes by rememberSaveable { mutableStateOf("") }
    var satisfaction by rememberSaveable { mutableStateOf(satisfactionLevels.last()) }
    var status by rememberSaveable { mutableStateOf("Draft Tersimpan") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(JournalBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            JournalBanner()

            JournalTextField(
                value = favoriteSpot,
                onValueChange = {
                    favoriteSpot = it
                    status = "Draft Tersimpan"
                },
                label = "SPOT WISATA FAVORIT",
                singleLine = true
            )

            JournalTextField(
                value = experience,
                onValueChange = {
                    experience = it
                    status = "Draft Tersimpan"
                },
                label = "APA YANG PALING KAMU NIKMATI?",
                placeholder = "Ceritakan pengalamanmu di sini...",
                singleLine = false,
                minLines = 3
            )

            JournalTextField(
                value = extraNotes,
                onValueChange = {
                    extraNotes = it
                    status = "Draft Tersimpan"
                },
                label = "CATATAN TAMBAHAN / PERLENGKAPAN",
                placeholder = "(Ketik perlengkapan ekstra di sini...)",
                singleLine = false,
                minLines = 1
            )

            Text(
                text = "TINGKAT KEPUASAN:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = JournalTextSecondary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                satisfactionLevels.forEach { level ->
                    SatisfactionChip(
                        text = level,
                        selected = level == satisfaction,
                        onClick = { satisfaction = level }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 36.dp)
        ) {
            Text("Status: ", fontSize = 12.sp, color = JournalTextSecondary)
            Text(
                text = status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = JournalAccent
            )
        }

        FloatingActionButton(
            onClick = {
                val message = if (favoriteSpot.isBlank()) {
                    "Isi nama spot wisata favoritmu dulu ya!"
                } else {
                    "Jurnal \"${favoriteSpot.trim()}\" berhasil disimpan! ($satisfaction)"
                }
                if (favoriteSpot.isNotBlank()) status = "Jurnal Disimpan"
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 24.dp),
            containerColor = JournalAccent,
            contentColor = JournalBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("+", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun JournalBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF1E3A8A), Color(0xFF2F6FC4))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "LOG PERJALANAN",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFCFE3FF)
            )
            Text(
                text = "Tromsø, Norway ❄",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Text(
                text = "Ekspedisi Aurora Borealis",
                fontSize = 12.sp,
                color = Color(0xFFCFE3FF)
            )
        }
    }
}

@Composable
private fun JournalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
        placeholder = placeholder?.let { { Text(it) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else minLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = JournalTextPrimary,
            unfocusedTextColor = JournalTextPrimary,
            focusedContainerColor = JournalCard,
            unfocusedContainerColor = JournalCard,
            focusedBorderColor = JournalAccent,
            unfocusedBorderColor = JournalBorder,
            focusedLabelColor = JournalAccent,
            unfocusedLabelColor = JournalTextSecondary,
            cursorColor = JournalAccent,
            focusedPlaceholderColor = JournalTextSecondary.copy(alpha = 0.7f),
            unfocusedPlaceholderColor = JournalTextSecondary.copy(alpha = 0.7f)
        )
    )
}

@Composable
private fun SatisfactionChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(
                if (selected) JournalAccent else Color.Transparent,
                RoundedCornerShape(50)
            )
            .border(
                1.dp,
                if (selected) JournalAccent else JournalBorder,
                RoundedCornerShape(50)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) JournalBackground else JournalTextSecondary
        )
    }
}