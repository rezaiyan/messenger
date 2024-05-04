package com.behzad.messenger.di

import android.content.Context
import com.behzad.messenger.utils.PhoneNumberRetriever
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @ActivityScoped
    fun providePhoneNumberRetriever(@ApplicationContext context: Context): PhoneNumberRetriever {
        return PhoneNumberRetriever(context)
    }

}
