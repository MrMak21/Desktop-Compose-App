package useCase

import contracts.PdfRepositoryContract
import java.io.File

class RenamePdfUseCase(private val pdfRepository: PdfRepositoryContract) {

    suspend operator fun invoke(pdfFile: File, newName: String) = pdfRepository.renameFile(pdfFile, newName)
}