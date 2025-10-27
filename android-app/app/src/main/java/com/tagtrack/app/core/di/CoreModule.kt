package com.tagtrack.app.core.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tagtrack.app.core.common.AppTheme
import com.tagtrack.app.core.data.AppSettingsRepository
import com.tagtrack.app.core.data.ItemRepositoryImpl
import com.tagtrack.app.core.data.OutfitRepositoryImpl
import com.tagtrack.app.core.data.remote.FirebaseAuthRepository
import com.tagtrack.app.core.data.local.DefaultAppSettingsRepository
import com.tagtrack.app.core.data.local.db.TagTrackDatabase
import com.tagtrack.app.core.domain.repository.ItemRepository
import com.tagtrack.app.core.domain.repository.AuthRepository
import com.tagtrack.app.core.domain.repository.OutfitRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreModule {
    @Binds
    abstract fun bindAppSettingsRepository(impl: DefaultAppSettingsRepository): AppSettingsRepository

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): TagTrackDatabase =
            Room.databaseBuilder(context, TagTrackDatabase::class.java, "tagtrack.db").build()

        @Provides
        fun provideItemDao(database: TagTrackDatabase) = database.itemDao()

        @Provides
        fun provideOutfitDao(database: TagTrackDatabase) = database.outfitDao()

        @Provides
        @Singleton
        fun provideItemRepository(impl: ItemRepositoryImpl): ItemRepository = impl

        @Provides
        @Singleton
        fun provideOutfitRepository(impl: OutfitRepositoryImpl): OutfitRepository = impl

        @Provides
        @Singleton
        fun provideAuthRepository(impl: FirebaseAuthRepository): AuthRepository = impl

        @Provides
        fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

        @Provides
        fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @Provides
        fun provideStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    }
}
