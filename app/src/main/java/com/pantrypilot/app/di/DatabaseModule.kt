package com.pantrypilot.app.di

import android.content.Context
import androidx.room.Room
import com.pantrypilot.app.data.local.AppDatabase
import com.pantrypilot.app.data.local.dao.RecipeDao
import com.pantrypilot.app.data.repository.SavedRecipeRepository
import com.pantrypilot.app.data.repository.SavedRecipeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "pantry_pilot_database"

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(database: AppDatabase): RecipeDao {
        return database.recipeDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryBindingModule {

    @Binds
    @Singleton
    abstract fun bindSavedRecipeRepository(
        impl: SavedRecipeRepositoryImpl
    ): SavedRecipeRepository
}
