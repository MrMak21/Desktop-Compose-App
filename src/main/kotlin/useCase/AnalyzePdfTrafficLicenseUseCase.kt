package useCase

import contracts.PdfRepositoryContract
import java.io.File

class AnalyzePdfTrafficLicenseUseCase(private val pdfRepository: PdfRepositoryContract) {

    suspend operator fun invoke(pdfFile: File) = pdfRepository.getVehicleIdFromImagePdf(pdfFile)
}