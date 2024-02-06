package useCase

import contracts.PdfRepositoryContract
import java.io.File

class AnalyzePdfKteoUseCase(private val pdfRepository: PdfRepositoryContract) {

    suspend operator fun invoke(pdfFile: File) = pdfRepository.getDateFromKteoPdf(pdfFile)
}