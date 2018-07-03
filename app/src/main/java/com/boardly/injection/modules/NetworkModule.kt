package com.boardly.injection.modules

import com.boardly.BuildConfig
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BGG_API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
    }
}