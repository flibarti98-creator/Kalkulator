package com.example.kalkulator

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    // 1. Kolejność działań
    @Test
    fun kolejnoscDzialan_zwracaPoprawnyWynik() {
        assertEquals("6", obliczWyrazenie("2+2*2"))
    }

    // 2. Nawiasy
    @Test
    fun nawiasy_majaWyższyPriorytet() {
        assertEquals("8", obliczWyrazenie("(2+2)*2"))
    }

    // 3. Dzielenie przez zero
    @Test
    fun dzieleniePrzezZero_zwracaKomunikatBezAwarii() {
        assertEquals("Błąd: dzielenie przez 0", obliczWyrazenie("1/0"))
    }

    // 4. Wyrażenie niedokończone
    @Test
    fun wyrazenieNiedokonczone_brakWynikuBezAwarii() {
        assertEquals("2+", obliczWyrazenie("2+"))
    }

    // 5. Nawias niezamknięty
    @Test
    fun nawiasNiezamkniety_brakWynikuBezAwarii() {
        assertEquals("((2+2)", obliczWyrazenie("((2+2)"))
    }

    // 6. Puste pole / stan początkowy
    @Test
    fun pustePole_brakWynikuIBrakiBledu() {
        assertEquals("", obliczWyrazenie(""))
        assertEquals("0", obliczWyrazenie("0"))
    }

    // 7. Dziedzina funkcji
    @Test
    fun pierwiastekZUjemnej_zwracaKomunikatBledu() {
        assertEquals("Błąd: dziedzina funkcji", obliczWyrazenie("sqrt(-1)"))
    }

    // 8. Duże liczby
    @Test
    fun duzeLiczby_obliczaPoprawnie() {
        val wynik = obliczWyrazenie("1234567890*1234567890")
        assertEquals("1524157875019052000", wynik)
    }

    // 9. Separator dziesiętny: kropka vs przecinek
    @Test
    fun separatorDziesietny_kropkaIPrzecinekDajaTenSamWynik() {
        assertEquals("3.5", obliczWyrazenie("2.5+1"))
        assertEquals("3.5", obliczWyrazenie("2,5+1"))
    }
}