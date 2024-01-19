package operations

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

class FileChooser {

    fun selectMutliplePDF(): List<File> {
        val fileChooser = JFileChooser().apply {
            isAcceptAllFileFilterUsed = false
            addChoosableFileFilter(FileNameExtensionFilter("PDF Files", "pdf"))
            fileSelectionMode = JFileChooser.FILES_ONLY
            isMultiSelectionEnabled = true
        }

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedPdfs = fileChooser.selectedFiles
            val selectedFiles = arrayListOf<File>()

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
}