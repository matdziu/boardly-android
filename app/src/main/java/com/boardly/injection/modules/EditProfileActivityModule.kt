package com.boardly.injection.modules

import com.boardly.editprofile.EditProfileInteractor
import com.boardly.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class EditProfileActivityModule {

    @Provides
    @ActivityScope
    fun provideEditProfileInteractor(): EditProfileInteractor {
        return EditProfileInteractor()
    }
}