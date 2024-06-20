package contracts

import java.io.File

interface PdfRepositoryContract {

    fun getVehicleIdFromPdf(pdfFile: File): String?
    fun getDateFromKteoPdf(pdfFile: File): String?
    fun renameFile(pdfFile: File, newName: String): File?
}