package repository

import contracts.PdfRepositoryContract
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File

class PdfRepository: PdfRepositoryContract {

    val PATTERN = "[A-Za-z]{3}\\d{4}"
    val GREEK_PATTERN = "[\\u0370-\\u03FF\\u1F00-\\u1FFF]{3}\\d{4}"

    val pdfStripper = PDFTextStripper()

    override fun getVehicleIdFromPdf(pdfFile: File): String? {
        val pdfDocument = Loader.loadPDF(pdfFile)
        pdfDocument?.let {
            val pdfText = pdfStripper.getText(it)
            return extractNumber(pdfText)
        }
        return null
    }

    private fun extractNumber(document: String): String? {
        println(document)
        val greekRegex = GREEK_PATTERN.toRegex()
        val regex = PATTERN.toRegex()
        val greekMatch = greekRegex.find(document)
        val match = regex.find(document)

        var greekResult = greekMatch?.value
        var result = match?.value

        return greekResult ?: result
    }
}