import extensions.isVehiclePlateLetters
import extensions.isVehiclePlateNumbers
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class VehiclePlateTest {


    @Test
    fun testVehiclePlateNumbersRegex() {
        assertEquals(true, "4568.0".isVehiclePlateNumbers())
        assertEquals(true, 2568.0.isVehiclePlateNumbers())

        assertEquals(false, "4.0".isVehiclePlateNumbers())
        assertEquals(false, "42.0".isVehiclePlateNumbers())
        assertEquals(false, "423.0".isVehiclePlateNumbers())
        assertEquals(false, "4568".isVehiclePlateNumbers())
        assertEquals(false, "4568a".isVehiclePlateNumbers())
        assertEquals(false, "487".isVehiclePlateNumbers())
        assertEquals(false, "42".isVehiclePlateNumbers())
        assertEquals(false, "41".isVehiclePlateNumbers())
        assertEquals(false, "4158455".isVehiclePlateNumbers())
        assertEquals(false, "avsds".isVehiclePlateNumbers())
        assertEquals(false, "".isVehiclePlateNumbers())

        assertEquals(false, 25.0.isVehiclePlateNumbers())
        assertEquals(false, 256.0.isVehiclePlateNumbers())
        assertEquals(false, 2.0.isVehiclePlateNumbers())
    }

    @Test
    fun testVehiclePlateLettersRegex() {
        assertEquals(true, "YPI".isVehiclePlateLetters())
        assertEquals(true, "TPH".isVehiclePlateLetters())
        assertEquals(true, "ΡΟΔ".isVehiclePlateLetters())
        assertEquals(true, "ΙΤΡ".isVehiclePlateLetters())
        assertEquals(true, "ΤΡΖ".isVehiclePlateLetters())

        assertEquals(false, "t5rd".isVehiclePlateLetters())
        assertEquals(false, "4568".isVehiclePlateLetters())
        assertEquals(false, "456".isVehiclePlateLetters())
        assertEquals(false, "G PO".isVehiclePlateLetters())
    }

}