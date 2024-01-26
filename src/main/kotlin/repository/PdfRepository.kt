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
        pdfStripper.addMoreFormatting
        val pdfDocument = Loader.loadPDF(pdfFile)
        pdfDocument?.let {
            val pdfText = pdfStripper.getText(it)
            return extractNumber(pdfText)
        }
        return null
    }

    override fun renameFile(pdfFile: File, newName: String): Boolean {
        val newFile = File(pdfFile.parent, "$newName.pdf")
        return pdfFile.renameTo(newFile)
    }

    private fun extractNumber(document: String): String? {
        val greekRegex = GREEK_PATTERN.toRegex()
        val greekMatch = greekRegex.find(document)
        val greekResult = greekMatch?.value

        if (greekResult != null) {
            println("Found greek: $greekResult")
        }

        return greekResult ?: extractEnglishNumber(document)
    }

    private fun extractEnglishNumber(document: String): String? {
        val englishRegex = PATTERN.toRegex()
        val englishMatch = englishRegex.find(document)
        val englishResult = englishMatch?.value

        if (englishResult != null) {
            println("Found english: $englishResult")
            val convertedGreekString = translateToGreek(englishResult)
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
}