package com.example.kalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalkulator.ui.theme.KalkulatorTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KalkulatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    KalkulatorEkran(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun KalkulatorEkran(modifier: Modifier = Modifier) {
    var wyswietlacz by rememberSaveable { mutableStateOf("0") }

    val rzedyPrzyciskow = listOf(
        listOf("C", "⌫", "(", ")"),
        listOf("7", "8", "9", "/"),
        listOf("4", "5", "6", "*"),
        listOf("1", "2", "3", "-"),
        listOf("0", ".", "+", "=")
    )

    val stanPrzewijania = rememberScrollState()

    LaunchedEffect(wyswietlacz) {
        stanPrzewijania.scrollTo(stanPrzewijania.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = wyswietlacz,
                fontSize = 48.sp,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.horizontalScroll(stanPrzewijania)
            )
        }

        rzedyPrzyciskow.forEach { rzad ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rzad.forEach { symbol ->
                    Button(
                        onClick = {
                            wyswietlacz = when (symbol) {
                                "C" -> "0"
                                "⌫" -> if (wyswietlacz.length > 1) wyswietlacz.dropLast(1) else "0"
                                else -> if (wyswietlacz == "0") symbol else wyswietlacz + symbol
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(text = symbol, fontSize = 22.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KalkulatorPreview() {
    KalkulatorTheme {
        KalkulatorEkran()
    }
}