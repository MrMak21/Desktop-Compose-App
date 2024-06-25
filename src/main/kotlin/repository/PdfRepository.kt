package repository

import contracts.PdfRepositoryContract
import extensions.translateToGreek
import helpers.Constants
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import org.apache.pdfbox.Loader
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.ImageType
import org.apache.pdfbox.rendering.PDFRenderer
import org.apache.pdfbox.text.PDFTextStripper
import java.awt.image.BufferedImage
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
                pdfText = extractTextFromPdf(it)
            }
            return extractKteoDate(pdfText)
        }
        return null
    }

    override fun getVehicleIdFromImagePdf(pdfFile: File): String? {
        val pdfDocument = Loader.loadPDF(pdfFile)
        return pdfDocument?.let {
            val pdfText = extractTextFromPdf(it)
            extractTrafficLicensePlate(pdfText)
        }
    }

    override fun renameFile(pdfFile: File, newName: String): File? {
        val newFile = File(pdfFile.parent, "$newName.pdf")
        if (pdfFile.renameTo(newFile)) {
            return newFile
        }
        return null
    }


    private fun extractTextFromPdf(pdDocument: PDDocument): String {

        val pdfRenderer = PDFRenderer(pdDocument)
        val out = java.lang.StringBuilder()

        val tesseractOcr: ITesseract = Tesseract()
        val dataDirectory: Path = Paths.get("src/main/resources/tessdata/")
        tesseractOcr.setDatapath(dataDirectory.toString())
        tesseractOcr.setLanguage("ell")
        tesseractOcr.setOcrEngineMode(2)
        tesseractOcr.setPageSegMode(1)
//        tesseractOcr.setTessVariable("tessedit_char_whitelist", "0123456789ΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΤΥΦΧΨΩαβγδεζθηικλμνξοπρστυφχψω ")  // Only greek letters and numbers

        for (page in 0 until pdDocument.numberOfPages) {

            val bim = pdfRenderer.renderImageWithDPI(page, 300f, ImageType.RGB)
            val preprocessedImage = preprocessImage(bim)
            val temp = File.createTempFile("tempfile_$page", ".png")

            try {
                ImageIO.write(bim, "png", temp)
                val result = tesseractOcr.doOCR(temp)
                out.append(result)
                temp.delete()
            } catch (t: Throwable) {
                temp.delete()
            }
        }
        return out.toString()
    }

    private fun preprocessImage(image: BufferedImage): BufferedImage {
        // Convert to grayscale
        val grayscaleImage = BufferedImage(image.width, image.height, BufferedImage.TYPE_BYTE_GRAY)
        val g = grayscaleImage.createGraphics()
        g.drawImage(image, 0, 0, null)
        g.dispose()

        // Additional preprocessing steps can be added here (e.g., noise reduction, deskewing)
        return grayscaleImage
    }

    private fun extractNumber(document: String): String? {
        val greekRegex = Constants.Vehicle.GREEK_LICENSE_FULL_REGEX.toRegex()
        val greekMatch = greekRegex.find(document)
        val greekResult = greekMatch?.value

        if (greekResult != null) {
            println("Found greek: $greekResult")
        }

        return greekResult ?: extractEnglishNumber(document)
    }

    private fun extractEnglishNumber(document: String): String? {
        val englishRegex = Constants.Vehicle.LICENSE_FULL_REGEX.toRegex()
        val englishMatch = englishRegex.find(document)
        val englishResult = englishMatch?.value

        if (englishResult != null) {
            println("Found english: $englishResult")
            val convertedGreekString = englishResult.translateToGreek()
            println("Translated in Greek: $convertedGreekString")
            return convertedGreekString
        }
        return englishResult
    }

    private fun extractTrafficLicensePlate(document: String): String? {
        val greekRegex = Constants.Vehicle.GREEK_LICENSE_FULL_REGEX_ALTERNATIVE.toRegex()
        val greekMatch = greekRegex.find(document)

        return greekMatch?.value
    }

    private fun extractKteoDate(document: String): String? {
        println(document)
        val dateRegex = KTEO_DATE_PATTERN.toRegex()
        val dateMatch = dateRegex.find(document)
        val dateResult = dateMatch?.value

        return dateResult
    }
}