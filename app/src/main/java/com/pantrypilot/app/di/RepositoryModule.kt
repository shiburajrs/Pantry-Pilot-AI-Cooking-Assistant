package com.pantrypilot.app.di

import com.google.firebase.ai.GenerativeModel
import com.pantrypilot.app.data.remote.IngredientRecognitionRepository
import com.pantrypilot.app.data.remote.RecipeDiscoveryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideIngredientRecognitionRepository(
        generativeModel: GenerativeModel
    ): IngredientRecognitionRepository {
        return IngredientRecognitionRepository(generativeModel)
    }

    @Provides
    @Singleton
    fun provideRecipeDiscoveryRepository(
        generativeModel: GenerativeModel
    ): RecipeDiscoveryRepository {
        return RecipeDiscoveryRepository(generativeModel)
    }
}
