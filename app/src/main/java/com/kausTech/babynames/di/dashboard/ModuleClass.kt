package com.kausTech.babynames.di.dashboard

import com.kausTech.network.DataManager
import dagger.Module
import dagger.Provides

@Module
class ModuleClass {
    // here is the simple code, but suppose that we provide something important here
    @Provides
    @DashboardScope
    fun provideViewModel(dataManager: DataManager): DashViewModel = DashViewModel(dataManager)
}