package com.example.week3homework.Soal1Bonus

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

// ---------- Palet warna ----------
private val CoffeeBackground = Color(0xFFF9F4F1)
private val CoffeeBrownDark = Color(0xFF4A1F10)
private val CoffeeOrange = Color(0xFFD9622B)
private val CoffeePeach = Color(0xFFFBE3CF)
private val CoffeeBorder = Color(0xFFF6D9BF)
private val CoffeeSummaryBg = Color(0xFFFBF3E8)

// ---------- Konstanta bisnis ----------
private const val BASE_PRICE = 25_000   // harga satuan Caramel Latte
private const val TAX_PERCENT = 10      // pajak resto 10%
private const val MIN_QUANTITY = 1      // kuantitas minimal

/** Pilihan ukuran beserta biaya tambahan. */
private enum class CupSize(val label: String, val extraPrice: Int) {
    REGULAR("Regular (+0)", 0),
    LARGE("Large (+6k)", 6_000)
}

/** Format angka menjadi "Rp 25.000". */
private fun formatRupiah(amount: Int): String {
    val formatted = NumberFormat.getInstance(Locale("id", "ID")).format(amount)
    return "Rp $formatted"
}

/**
 * Layar "Coffee Order Display".
 *
 * State yang digunakan (Event-Driven State):
 *  - quantity     : jumlah pesanan, berubah ketika tombol (+) / (-) diklik
 *  - selectedSize : ukuran gelas yang dipilih
 * Subtotal, pajak, dan total diturunkan (derived) dari state sehingga
 * otomatis ter-update setiap state berubah.
 */
@Composable
fun CoffeeOrderScreen(modifier: Modifier = Modifier) {
    var quantity by rememberSaveable { mutableIntStateOf(3) }
    var selectedSize by rememberSaveable { mutableStateOf(CupSize.REGULAR) }

    // ----- Perhitungan harga dinamis -----
    val unitPrice = BASE_PRICE + selectedSize.extraPrice
    val subtotal = unitPrice * quantity
    val tax = subtotal * TAX_PERCENT / 100
    val total = subtotal + tax

    // ----- Snackbar untuk pesan konfirmasi -----
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CoffeeBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            CoffeeHeader()
            Spacer(Modifier.height(12.dp))

            ProductCard(unitPrice = unitPrice)
            Spacer(Modifier.height(16.dp))

            SizeSelector(
                selected = selectedSize,
                onSelect = { selectedSize = it }
            )
            Spacer(Modifier.height(12.dp))

            QuantityCard(
                quantity = quantity,
                onIncrease = { quantity++ },
                // Batas minimal 1: tidak boleh 0 atau negatif
                onDecrease = { if (quantity > MIN_QUANTITY) quantity-- }
            )
            Spacer(Modifier.height(12.dp))

            SummaryBox(subtotal = subtotal, tax = tax, total = total)

            // Dorong tombol ke bagian bawah layar
            Spacer(Modifier.weight(1f))

            AddToCartButton(
                onClick = {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            "$quantity Caramel Latte (${selectedSize.name.lowercase()
                                .replaceFirstChar { it.uppercase() }}) ditambahkan ke keranjang. " +
                                    "Total ${formatRupiah(total)}"
                        )
                    }
                }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
        )
    }
}

// =====================================================================
// Komponen-komponen kecil
// =====================================================================

@Composable
private fun CoffeeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Kopi Kenangan Senja",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = CoffeeBrownDark
        )
        Text(text = "☕", fontSize = 22.sp)
    }
}

@Composable
private fun ProductCard(unitPrice: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CoffeeBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Banner gambar (gradien coklat) + ikon cangkir di tengah
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF6D3219), Color(0xFF8C3B1C))
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "☕", fontSize = 64.sp)
            }

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Caramel Latte",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CoffeeBrownDark
            )
            Text(
                text = "Espresso shot, steamed milk & caramel syrup",
                fontSize = 11.sp,
                color = Color(0xFF8C4A2F),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "${formatRupiah(unitPrice)} / cup",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CoffeeOrange
            )
        }
    }
}

@Composable
private fun SizeSelector(selected: CupSize, onSelect: (CupSize) -> Unit) {
    Text(
        text = "PILIHAN UKURAN:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF7A3B22)
    )
    Spacer(Modifier.height(6.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CupSize.entries.forEach { size ->
            val isSelected = size == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(size) }
                    .background(
                        if (isSelected) Color.White else CoffeeBackground,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) CoffeeOrange else CoffeeBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = size.label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) CoffeeOrange else CoffeeBrownDark
                )
            }
        }
    }
}

@Composable
private fun QuantityCard(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CoffeeBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Jumlah Pesanan:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = CoffeeBrownDark
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                QuantityButton(
                    symbol = "-",
                    background = Color.White,
                    contentColor = CoffeeBrownDark,
                    onClick = onDecrease
                )
                Text(
                    text = quantity.toString(),
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = CoffeeBrownDark
                )
                QuantityButton(
                    symbol = "+",
                    background = CoffeePeach,
                    contentColor = CoffeeOrange,
                    onClick = onIncrease
                )
            }
        }
    }
}

@Composable
private fun QuantityButton(
    symbol: String,
    background: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(background, RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE0DAD6), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun SummaryBox(subtotal: Int, tax: Int, total: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CoffeeSummaryBg, RoundedCornerShape(12.dp))
            .dashedBorder(CoffeeBorder, radius = 12.dp)
            .padding(12.dp)
    ) {
        SummaryRow("Subtotal:", formatRupiah(subtotal))
        Spacer(Modifier.height(4.dp))
        SummaryRow("Pajak Resto ($TAX_PERCENT%):", formatRupiah(tax))
        Spacer(Modifier.height(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CoffeeBorder)
        )
        Spacer(Modifier.height(6.dp))
        SummaryRow(
            label = "Total Tagihan:",
            value = formatRupiah(total),
            bold = true,
            valueColor = CoffeeOrange,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    bold: Boolean = false,
    valueColor: Color = CoffeeBrownDark,
    fontSize: TextUnit = 12.sp
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = fontSize,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = CoffeeBrownDark
        )
        Text(
            text = value,
            fontSize = fontSize,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = valueColor
        )
    }
}

@Composable
private fun AddToCartButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(CoffeeOrange, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick) // event onClick
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "TAMBAH KE KERANJANG",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

/** Modifier custom untuk border putus-putus (dashed). */
private fun Modifier.dashedBorder(color: Color, radius: Dp, strokeWidth: Dp = 1.dp): Modifier =
    this.drawBehind {
        drawRoundRect(
            color = color,
            cornerRadius = CornerRadius(radius.toPx()),
            style = Stroke(
                width = strokeWidth.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f))
            )
        )
    }