package operations.excel


import extensions.*
import helpers.Constants
import org.apache.commons.math3.geometry.partitioning.Side
import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import ui.sidePanel.SidePanelItem
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ExcelHandler(private val workbook: Workbook, private val excelFile: File) {

    companion object {
        fun initExcelHandler(excelFile: File): ExcelHandler? {
            val workbook = XSSFWorkbook(FileInputStream(excelFile)) ?: return null
            return ExcelHandler(workbook, excelFile)
        }
    }

    private var TRAFFIC_FEES_COLUMN_INDEX = -1
    private var TRAFFIC_LICENSE_COLUMN_INDEX = -1

    private var VEHICLE_LICENSE_PLATE_COLUMN_INDEX = -1
    private var VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX = -1

    private var activeSheet: Sheet? = null

    init {
        activeSheet = workbook.getSheetAt(0)
        activeSheet?.let { activesheet ->
            findTrafficFeesExcelHeaderCell(activesheet)
            findTrafficLicenseExcelHeaderCell(activesheet)
            findVehiclePlateColumnIndex(activesheet)
        }
    }

    private fun findTrafficLicenseExcelHeaderCell(sheet: Sheet) {
        val firstRow = sheet.getRow(0) ?: return
        for (cell in firstRow) {
            cell?.let { currentCell ->
                if (currentCell.hasStringValue(Constants.ExcelColumnHeaders.TRAFFIC_LICENSE_HEADER_LABEL)) {
                    TRAFFIC_LICENSE_COLUMN_INDEX = currentCell.columnIndex
                }
            }
        }
    }

    private fun findTrafficFeesExcelHeaderCell(sheet: Sheet) {
        val firstRow = sheet.getRow(0) ?: return
        for (cell in firstRow) {
            cell?.let { currentCell ->
                if (currentCell.hasStringValue(Constants.ExcelColumnHeaders.TRAFFIC_FEES_LABEL)) {
                    TRAFFIC_FEES_COLUMN_INDEX = currentCell.columnIndex
                }
            }
        }
    }

    private fun findVehiclePlateColumnIndex(sheet: Sheet) {
        for (row in sheet) {
            for (cell in row) {
                cell?.let { currentCell ->
                    if (currentCell.cellType == CellType.STRING && currentCell.stringCellValue.isVehiclePlateLetters()) {
                        VEHICLE_LICENSE_PLATE_COLUMN_INDEX = currentCell.columnIndex
                    }
                    if (currentCell.cellType == CellType.NUMERIC && currentCell.numericCellValue.isVehiclePlateNumbers()) {
                        VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX = currentCell.columnIndex
                    }
                    if (VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX != -1 && VEHICLE_LICENSE_PLATE_COLUMN_INDEX != -1) {
                        return
                    }
                }
            }
        }
    }

    fun writeVehicleIdToExcel(vehicleId: String, type: SidePanelItem): Boolean {
        return when (type) {
            SidePanelItem.TRAFFIC_FEES -> { writeVehicleToExcel(vehicleId, type) }
            SidePanelItem.TRAFFIC_LICENSE -> { writeVehicleToExcel(vehicleId, type) }
            SidePanelItem.KTEO -> { return false }
        }
    }

    private fun writeVehicleToExcel(vehicleId: String, type: SidePanelItem): Boolean {
        val vehicleIdLettersGR = vehicleId.replace(" ", "").substring(0,3).trim()
        val vehicleIdNumbersGR = vehicleId.replace(" ", "").substring(3,7).trim()

        val vehicleIdLettersRow = findVehicleIdLettersCell(vehicleIdLettersGR)?.row?.rowNum ?: return false
        val vehicleIdNumbersRow = findVehicleIdNumbersCell(vehicleIdNumbersGR)?.row?.rowNum ?: return false


        return if (vehicleIdLettersRow != vehicleIdNumbersRow) {
            val finalFoundRow = findRowContainBothValues(vehicleIdLettersRow, vehicleIdNumbersRow, vehicleIdLettersGR, vehicleIdNumbersGR)
            writeInExcel(finalFoundRow, getColumnIndexFromType(type))
        } else {
            writeInExcel(vehicleIdLettersRow, getColumnIndexFromType(type))
        }
    }

    private fun getColumnIndexFromType(type: SidePanelItem): Int =
        when (type) {
            SidePanelItem.TRAFFIC_FEES -> { TRAFFIC_FEES_COLUMN_INDEX }
            SidePanelItem.TRAFFIC_LICENSE -> { TRAFFIC_LICENSE_COLUMN_INDEX }
            else -> { -1 }
        }


    private fun writeInExcel(row: Int, column: Int): Boolean {
        return try {
            activeSheet?.let { activeSheet ->
                val currRow = activeSheet.getRow(row) ?: return false
                val cell = currRow.getCell(column) ?: return false
                cell.setCellValue("OK!")

                saveAndCloseExcelFile()

                true
            } ?: false
        } catch (t: Throwable) {
            false
        }
    }

    private fun saveAndCloseExcelFile() {
        FileOutputStream(excelFile).use { outputStream ->
            workbook.write(outputStream)
            outputStream.close()
        }
    }

    private fun findRowContainBothValues(lettersRow: Int, numbersRow: Int, lettersValue: String, numbersValue: String): Int {
        activeSheet?.let { activeSheet ->
            val lettersExcelRow = activeSheet.getRow(lettersRow)
            val numbersExcelRow = activeSheet.getRow(numbersRow)

            val lettersRow_lettersValueFound = lettersExcelRow?.find { it.hasStringValue(lettersValue) }
            val lettersRow_numbersFoundValue = lettersExcelRow?.find { it.hasNumericValue(numbersValue) }

            val numbersRow_lettersValueFound = numbersExcelRow?.find { it.hasStringValue(lettersValue) }
            val numbersRow_numbersFoundValue = numbersExcelRow?.find { it.hasNumericValue(numbersValue) }

            if (lettersRow_lettersValueFound != null && lettersRow_numbersFoundValue != null) {
                lettersRow_lettersValueFound.row?.let {
                    return it.rowNum
                }
            }

            if (numbersRow_lettersValueFound != null && numbersRow_numbersFoundValue != null) {
                numbersRow_lettersValueFound.row?.let {
                    return it.rowNum
                }
            }

            return -1

        }
        return -1
    }

    private fun findVehicleIdLettersCell(vehicleId: String): Cell? {
        if (VEHICLE_LICENSE_PLATE_COLUMN_INDEX == -1) {
            return null
        }

        return activeSheet?.findCell(VEHICLE_LICENSE_PLATE_COLUMN_INDEX, vehicleId)
    }

    private fun findVehicleIdNumbersCell(vehicleIdNumbers: String): Cell? {
        if (VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX == -1) {
            return null
        }

        return activeSheet?.findCell(VEHICLE_LICENSE_NUMBERS_COLUMN_INDEX, vehicleIdNumbers)
    }


}