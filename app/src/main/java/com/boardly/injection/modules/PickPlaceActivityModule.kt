package com.boardly.injection.modules

import com.boardly.injection.ActivityScope
import com.boardly.retrofit.places.NominatimService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
class PickPlaceActivityModule {

    @Provides
    @ActivityScope
    fun provideNominatimService(retrofit: Retrofit): NominatimService {
        return retrofit.create(NominatimService::class.java)
    }
}