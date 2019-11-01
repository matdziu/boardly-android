package com.boardly.injection.modules

import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import dagger.Module
import dagger.Provides
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(tikXml: TikXml): Retrofit {
        return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(TikXmlConverterFactory.create(tikXml))
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