package com.boardly.injection.modules

import com.boardly.analytics.Analytics
import com.boardly.analytics.AnalyticsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalytics(analyticsImpl: AnalyticsImpl): Analytics {
        return analyticsImpl
    }
}