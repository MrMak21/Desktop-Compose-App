package repository

import contracts.PdfRepositoryContract
import extensions.translateToGreek
import helpers.Constants
import net.sourceforge.tess4j.Tesseract
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO


class PdfRepository: PdfRepositoryContract {

    val KTEO_DATE_PATTERN = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$"

    val pdfStripper = PDFTextStripper()

    override fun getVehicleIdFromPdf(pdfFile: File): String? {
        val pdfDocument = Loader.loadPDF(pdfFile)
        pdfDocument?.let {
            val pdfText = pdfStripper.getText(it)
            return extractNumber(pdfText)
        }
        return null
    }

    override fun getDateFromKteoPdf(pdfFile: File): String? {
        val pdfDocument = Loader.loadPDF(pdfFile)
        pdfDocument?.let {
            var pdfText = pdfStripper.getText(it)
            if (pdfText.trim().isEmpty()) {
                pdfText = extractTextFromImage(it)
            }
            return extractKteoDate(pdfText)
        }
        return null
    }

    private fun extractTextFromImage(pdDocument: PDDocument): String {

        val pdfRenderer = PDFRenderer(pdDocument)
        val out = java.lang.StringBuilder()

        val _tesseract = Tesseract()
        val dataDirectory: Path = Paths.get("src/main/resources/tessdata/")
        _tesseract.setDatapath(dataDirectory.toString())
        _tesseract.setLanguage("grc")
//        _tesseract.setTessVariable("tessedit_char_whitelist", "0123456789abcdefghijklmnopqrstuvwxyz")

        for (page in 0 until pdDocument.numberOfPages) {
            val bim = pdfRenderer.renderImageWithDPI(page, 300f, ImageType.RGB)

            // Create a temp image file
            val temp = File.createTempFile("tempfile_$page", ".png")
            ImageIO.write(bim, "png", temp)
            val result = _tesseract.doOCR(temp)
            out.append(result)

            // Delete temp file
            temp.delete()
        }
        return out.toString()
    }

    override fun renameFile(pdfFile: File, newName: String): File? {
        val newFile = File(pdfFile.parent, "$newName.pdf")
        if (pdfFile.renameTo(newFile)) {
            return newFile
        }
        return null
    }

    private fun extractNumber(document: String): String? {
        val greekRegex = Constants.Vehicle.GREEK_LICENSE_FULL_REGEX.toRegex()  // VEHICLE_ID_GREEK_PATTERN.toRegex()
        val greekMatch = greekRegex.find(document)
        val greekResult = greekMatch?.value

        if (greekResult != null) {
            println("Found greek: $greekResult")
        }

        return greekResult ?: extractEnglishNumber(document)
    }

    private fun extractEnglishNumber(document: String): String? {
        val englishRegex = Constants.Vehicle.LICENSE_FULL_REGEX.toRegex()  // VEHICLE_ID_PATTERN.toRegex()
        val englishMatch = englishRegex.find(document)
        val englishResult = englishMatch?.value

        if (englishResult != null) {
            println("Found english: $englishResult")
            val convertedGreekString = englishResult.translateToGreek() // translateToGreek(englishResult)
            println("Translated in Greek: $convertedGreekString")
            return convertedGreekString
        }
        return englishResult
    }

    private fun translateToGreek(input: String): String {
        val englishToGreek = mapOf(
            'a' to 'α', 'A' to 'Α',
            'b' to 'β', 'B' to 'Β',
            'e' to 'ε', 'E' to 'Ε',
            'h' to 'η', 'H' to 'Η',
            'i' to 'ι', 'I' to 'Ι',
            'k' to 'κ', 'K' to 'Κ',
            'm' to 'μ', 'M' to 'Μ',
            'n' to 'ν', 'N' to 'Ν',
            'o' to 'ο', 'O' to 'Ο',
            'p' to 'π', 'P' to 'Ρ',
            'r' to 'ρ', 'R' to 'Ρ',
            's' to 'σ', 'S' to 'Σ',
            't' to 'τ', 'T' to 'Τ',
            'u' to 'υ', 'U' to 'Υ',
            'x' to 'χ', 'X' to 'Χ',
            'y' to 'γ', 'Y' to 'Υ',
            'z' to 'ζ', 'Z' to 'Ζ'
        )

        return input.map { char ->
            englishToGreek[char] ?: char
        }.joinToString("")
    }

    private fun extractKteoDate(document: String): String? {
        println(document)
        val dateRegex = KTEO_DATE_PATTERN.toRegex()
        val dateMatch = dateRegex.find(document)
        val dateResult = dateMatch?.value

        return dateResult
    }
}