package operations

import operations.preferences.UserPreferences
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class FileChooser {

    fun selectMutliplePDF(): List<File> {
        val fileChooser = JFileChooser(getLastOpenFileLocation()).apply {
            dialogTitle = "Select pdf files"
            isAcceptAllFileFilterUsed = false
            addChoosableFileFilter(FileNameExtensionFilter("PDF Files", "pdf"))
            fileSelectionMode = JFileChooser.FILES_ONLY
            isMultiSelectionEnabled = true
        }

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedPdfs = fileChooser.selectedFiles
            val selectedFiles = arrayListOf<File>()

            if (selectedPdfs.isNotEmpty()) {
                val lastFolderSelected = selectedPdfs[0].parent
                UserPreferences.saveLastOpenPath(lastFolderSelected)
            }

            selectedPdfs.forEach {
                val file = File(it.absolutePath)
                if (file.exists()) {
                    selectedFiles.add(file)
                    println("Selected file: ${it.absolutePath}")
                }
            }
            return selectedFiles
        }
        return emptyList()
    }

    fun selectExcelFile(): File? {
        val fileChooser = JFileChooser(getLastOpenFileLocation()).apply {
            dialogTitle = "Select pdf files"
            isAcceptAllFileFilterUsed = false
            addChoosableFileFilter(FileNameExtensionFilter("Excel Files", "xlsx"))
            fileSelectionMode = JFileChooser.FILES_ONLY
            isMultiSelectionEnabled = true
        }

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedExcel = fileChooser.selectedFile
            return selectedExcel
        }

        return null
    }

    private fun getLastOpenFileLocation(): String {
        val lastOpenPath = UserPreferences.getLastOpenPath()
        return lastOpenPath.ifEmpty {
            System.getProperty("user.home") + File.separator + "Desktop"
        }
    }
}