package data.main

import java.io.File

data class PdfListFile(
    val file: File,
    var status: PdfFileStatus = PdfFileStatus.NONE
)

enum class PdfFileStatus {
    SUCCESS,
    FAILED,
    LOADING,
    SELECTED,
    NONE
}