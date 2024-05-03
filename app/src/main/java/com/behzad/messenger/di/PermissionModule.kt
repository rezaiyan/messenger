package com.behzad.messenger.di

import com.behzad.messenger.utils.GetUserPhoneNumberUseCase
import com.behzad.messenger.utils.GetUserPhoneNumberUseCaseImpl
import com.behzad.messenger.utils.PermissionHandler
import com.behzad.messenger.utils.PermissionHandlerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class PermissionModule {

    @Binds
    @ActivityScoped
    abstract fun bindGetUserPhoneNumberUseCase(impl: GetUserPhoneNumberUseCaseImpl): GetUserPhoneNumberUseCase
    @Binds
    @ActivityScoped
    abstract fun bindPermissionHandler(impl: PermissionHandlerImpl): PermissionHandler


}
