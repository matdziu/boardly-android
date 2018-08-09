package com.boardly.injection.modules

import com.boardly.editprofile.network.EditProfileService
import com.boardly.editprofile.network.EditProfileServiceImpl
import com.boardly.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class EditProfileActivityModule {

    @Provides
    @ActivityScope
    fun provideEditProfileService(): EditProfileService {
        return EditProfileServiceImpl()
    }
}