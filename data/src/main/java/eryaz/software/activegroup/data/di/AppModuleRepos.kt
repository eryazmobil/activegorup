package eryaz.software.activegroup.data.di

import eryaz.software.activegroup.data.repositories.AuthRepo
import eryaz.software.activegroup.data.repositories.BarcodeRepo
import eryaz.software.activegroup.data.repositories.CountingRepo
import eryaz.software.activegroup.data.repositories.OrderRepo
import eryaz.software.activegroup.data.repositories.PlacementRepo
import eryaz.software.activegroup.data.repositories.UserRepo
import eryaz.software.activegroup.data.repositories.WorkActivityRepo
import org.koin.dsl.module

val appModuleRepos = module {

    factory { AuthRepo(get()) }

    factory { UserRepo(get()) }

    factory { WorkActivityRepo(get()) }

    factory { BarcodeRepo(get()) }

    factory { PlacementRepo(get()) }

    factory { OrderRepo(get()) }

    factory { CountingRepo(get()) }

}