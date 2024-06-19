package extensions

import helpers.Constants
import org.apache.poi.ss.usermodel.*

fun String.isVehiclePlateLetters(): Boolean {
    return this.matches(Constants.Vehicle.LICENSE_LETTERS_REGEX.toRegex()) || this.matches(Constants.Vehicle.GREEK_LICENSE_LETTERS_REGEX.toRegex())
}

fun String.isVehiclePlateNumbers() = this.matches(Constants.vehiclePlateNumbersRegex)
fun Double.isVehiclePlateNumbers() = this.toString().isVehiclePlateNumbers()

fun Cell.hasStringValue(value: String): Boolean {
    return this.cellType == CellType.STRING && (this.stringCellValue == value || this.stringCellValue == value.translateToEnglish())
}

fun Cell.hasNumericValue(value: String): Boolean {
    return this.cellType == CellType.NUMERIC && this.numericCellValue == value.toDouble()
}

fun Sheet.findCell(index: Int, value: String): Cell? {
    for (row in this) {
        row?.let { currRow ->
            currRow.getCell(index)?.let { cell ->
                if (cell.hasStringValue(value)) {
                    return cell
                }
                if (cell.hasNumericValue(value)) {
                    return cell
                }
            }
        }
    }
    return null
}