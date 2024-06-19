import extensions.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LanguageTest {


    @Test
    fun testIsEnglishFun() {
        val englishText = "ABC"

        assertEquals(true, englishText.isEnglish())
        assertEquals(false, englishText.isGreek())
    }

    @Test
    fun testIsGreekFun() {
        val greekText = "ΑΕΚ"
        assertEquals(true, greekText.isGreek())
        assertEquals(false, greekText.isEnglish())
    }

    @Test
    fun testLanguages() {
        val englishText = "AHU"
        val greekText = "ΙΥΟ"

        assertEquals(true, englishText.isEnglish())
        assertEquals(false, englishText.isGreek())

        assertEquals(false, greekText.isEnglish())
        assertEquals(true, greekText.isGreek())
    }

    @Test
    fun translateToEnglish() {
        val greekText = "ΤΡΗ"
        val translatedText = greekText.translateToEnglish()

        assertEquals(true, translatedText.isEnglish())
        assertEquals(false, translatedText.isGreek())
        assertEquals("TPH", translatedText)
    }

    @Test
    fun translateToGreek() {
        val englishText = "YTO"
        val translatedText = englishText.translateToGreek()

        assertEquals(true, translatedText.isGreek())
        assertEquals(false, translatedText.isEnglish())
        assertEquals("ΥΤΟ", translatedText)
    }
}