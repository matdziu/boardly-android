package com.boardly.injection.modules

import com.boardly.BuildConfig
import com.tickaroo.tikxml.TikXml
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(tikXml: TikXml): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.NOMINATIM_API_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideTikXml(): TikXml {
        return TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .build()
    }
}