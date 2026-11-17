package com.example.kalkulator

import android.content.res.Configuration
import android.os.Bundle
import androidx.compose.ui.text.TextStyle
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kalkulator.ui.theme.KalkulatorTheme
import android.util.Log
import org.mariuszgromada.math.mxparser.Expression
import org.mariuszgromada.math.mxparser.License
import java.util.Locale

enum class TypPrzycisku {
    CYFRA,
    DZIALANIE,
    ROWNA_SIE,
    USUWANIE

}

data class PrzyciskDane(
    val etykieta: String,
    val tekstDoWpisania: String = etykieta,
    val typ: TypPrzycisku = TypPrzycisku.CYFRA
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        License.iConfirmNonCommercialUse("Student")
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
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d("KALKULATOR_STAN", "Zawartość Bundle: $outState")
        for (klucz in outState.keySet()) {
            Log.d("KALKULATOR_STAN", "Klucz: $klucz -> Wartość: ${outState.get(klucz)}")
        }
    }
}


@Composable
fun KalkulatorEkran(modifier: Modifier = Modifier) {
    var wyswietlacz by rememberSaveable { mutableStateOf("0") }
//Bundle
    val konfiguracja = LocalConfiguration.current
    val czyPoziomo = konfiguracja.orientation == Configuration.ORIENTATION_LANDSCAPE

    val przyciskiPodstawowe = listOf(
        listOf(
            PrzyciskDane("C", typ = TypPrzycisku.USUWANIE),
            PrzyciskDane("⌫", typ = TypPrzycisku.USUWANIE),
            PrzyciskDane("(", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane(")", typ = TypPrzycisku.DZIALANIE)
        ),
        listOf(
            PrzyciskDane("7"), PrzyciskDane("8"), PrzyciskDane("9"),
            PrzyciskDane("/", typ = TypPrzycisku.DZIALANIE)
        ),
        listOf(
            PrzyciskDane("4"), PrzyciskDane("5"), PrzyciskDane("6"),
            PrzyciskDane("*", typ = TypPrzycisku.DZIALANIE)
        ),
        listOf(
            PrzyciskDane("1"), PrzyciskDane("2"), PrzyciskDane("3"),
            PrzyciskDane("-", typ = TypPrzycisku.DZIALANIE)
        ),
        listOf(
            PrzyciskDane("0"), PrzyciskDane("."),
            PrzyciskDane("+", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("=", typ = TypPrzycisku.ROWNA_SIE)
        )
    )

    val przyciskiNaukowe = listOf(
        listOf(
            PrzyciskDane("sin", "sin(", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("cos", "cos(", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("tan", "tan(", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("π", "π", typ = TypPrzycisku.DZIALANIE),
        ),
        listOf(
            PrzyciskDane("√", "√(", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("x^y", "^", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("ln", "ln(", typ = TypPrzycisku.DZIALANIE),
            PrzyciskDane("e", "e", typ = TypPrzycisku.DZIALANIE),
        )
    )

    val obslugaKlikniecia: (PrzyciskDane) -> Unit = { przycisk ->
        val czyKomunikatBledu = wyswietlacz.startsWith("Błąd")

        wyswietlacz = when (przycisk.etykieta) {
            "C" -> "0"
            "⌫" -> if (czyKomunikatBledu || wyswietlacz.length <= 1) "0" else wyswietlacz.dropLast(1)
            "=" -> if (czyKomunikatBledu) "0" else obliczWyrazenie(wyswietlacz)
            else -> {
                if (wyswietlacz == "0" || czyKomunikatBledu) {
                    przycisk.tekstDoWpisania
                } else {
                    wyswietlacz + przycisk.tekstDoWpisania
                }
            }
        }
    }

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
                .weight(if (czyPoziomo) 1.5f else 2f),
            contentAlignment = Alignment.BottomEnd
        ) {
            Text(
                text = wyswietlacz,
                fontSize = if (czyPoziomo) 36.sp else 48.sp,
                textAlign = TextAlign.End,
                maxLines = 1,
                softWrap = false,
                style = TextStyle(fontFeatureSettings = "tnum"),
                modifier = Modifier.horizontalScroll(stanPrzewijania)
            )
        }

        if (czyPoziomo) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SiatkaPrzyciskow(
                    rzedy = przyciskiNaukowe,
                    onKlik = obslugaKlikniecia,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                SiatkaPrzyciskow(
                    rzedy = przyciskiPodstawowe,
                    onKlik = obslugaKlikniecia,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }
        } else {
            SiatkaPrzyciskow(
                rzedy = przyciskiPodstawowe,
                onKlik = obslugaKlikniecia,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f)
            )
        }
    }
}

@Composable
fun SiatkaPrzyciskow(
    rzedy: List<List<PrzyciskDane>>,
    onKlik: (PrzyciskDane) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rzedy.forEach { rzad ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rzad.forEach { przycisk ->
                    val (kolorTla, kolorTekstu) = when (przycisk.typ) {
                        TypPrzycisku.ROWNA_SIE -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
                        TypPrzycisku.DZIALANIE -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
                        TypPrzycisku.CYFRA -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
                        TypPrzycisku.USUWANIE -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
                    }

                    Button(
                        onClick = { onKlik(przycisk) },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = kolorTla,
                            contentColor = kolorTekstu
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = przycisk.etykieta,
                            fontSize = 18.sp,
                            maxLines = 1,
                            softWrap = false
                        )
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

fun obliczWyrazenie(wejscie: String): String {


    val oczyszczone = wejscie.trim()
    if (oczyszczone.isEmpty() || oczyszczone == "0") return oczyszczone

    if (oczyszczone.last() in listOf('+', '-', '*', '/', '^', '(', '.')) {
        return oczyszczone
    }

    val wyrazenieDlaParsera = oczyszczone
        .replace(",", ".")
        .replace("π", "pi")
        .replace("√(", "sqrt(")
        .replace("√", "sqrt")

    val expr = Expression(wyrazenieDlaParsera)

    if (!expr.checkSyntax()) {
        return oczyszczone
    }

    val wynik = expr.calculate()


    if (wynik.isNaN() || wynik.isInfinite()) {
        return if (wyrazenieDlaParsera.contains("sqrt(-")) {
            "Błąd: dziedzina funkcji"
        } else {
            "Błąd: dzielenie przez 0"
        }
    }

    return if (wynik % 1.0 == 0.0 && kotlin.math.abs(wynik) < 1e15) {
        wynik.toLong().toString()
    } else if (wynik % 1.0 == 0.0) {
        java.math.BigDecimal.valueOf(wynik).toPlainString()
    } else {
        String.format(Locale.US, "%.10f", wynik).trimEnd('0').trimEnd('.')
    }
}