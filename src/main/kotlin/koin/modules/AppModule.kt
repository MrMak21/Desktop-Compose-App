package koin.modules

import contracts.PdfRepositoryContract
import operations.FileChooser
import org.koin.dsl.module
import repository.PdfRepository
import useCase.AnalyzePdfTrafficLicenseUseCase
import useCase.AnalyzePdfVehicleIdUseCase
import useCase.RenamePdfUseCase
import viewModel.main.MainViewModel
import viewModel.trafficLicense.TrafficLicenseViewModel

val appModule = module {
     single {
         FileChooser()
     }

     single<PdfRepositoryContract> {
         PdfRepository()
     }

    single {
        AnalyzePdfVehicleIdUseCase(pdfRepository = get())
    }

    single {
        RenamePdfUseCase(pdfRepository = get())
    }

    single { AnalyzePdfTrafficLicenseUseCase(pdfRepository = get()) }

    single {
        MainViewModel(get(), get(), get(), get())
    }

    single {
        TrafficLicenseViewModel(get(), get())
    }
}