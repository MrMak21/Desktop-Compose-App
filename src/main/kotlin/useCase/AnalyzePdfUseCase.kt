package useCase

import contracts.PdfRepositoryContract
import java.io.File

class AnalyzePdfUseCase(private val pdfRepository: PdfRepositoryContract) {

    suspend operator fun invoke(pdfFile: File) = pdfRepository.getVehicleIdFromPdf(pdfFile)
}