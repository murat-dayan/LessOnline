package com.muratdayan.lessonline.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratdayan.lessonline.data.remote.repository.FirebaseRepository
import com.muratdayan.lessonline.presentation.util.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Provides
    @Singleton
    fun providePreferenceHelper(@ApplicationContext context: Context): PreferenceHelper {
        return PreferenceHelper(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseRepository(firebaseFirestore: FirebaseFirestore, firebaseAuth: FirebaseAuth): FirebaseRepository{
        return  FirebaseRepository(firebaseFirestore,firebaseAuth)
    }

}