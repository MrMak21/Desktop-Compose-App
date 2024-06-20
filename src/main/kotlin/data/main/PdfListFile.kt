package data.main

import java.io.File

data class PdfListFile(
    var file: File,
    var status: PdfFileStatus = PdfFileStatus.NONE
) {
    fun changeStatus(newStatus: PdfFileStatus) {
        status = newStatus
    }

    fun updateFileAndStatus(newFile: File, newStatus: PdfFileStatus) {
        file = newFile
        status = newStatus
    }
}

enum class PdfFileStatus {
    SUCCESS,
    FAILED,
    LOADING,
    SELECTED,
    NONE
}