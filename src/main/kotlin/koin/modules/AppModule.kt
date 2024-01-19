package koin.modules

import contracts.PdfRepositoryContract
import operations.FileChooser
import org.koin.dsl.module
import repository.PdfRepository
import useCase.AnalyzePdfUseCase
import viewModel.main.MainViewModel

val appModule = module {
     single {
         FileChooser()
     }

     single<PdfRepositoryContract> {
         PdfRepository()
     }

    single {
        AnalyzePdfUseCase(pdfRepository = get())
    }

    single {
        MainViewModel(get(), get())
    }
}