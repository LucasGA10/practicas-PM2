package ar.edu.unlam.mobile.scaffolding.di

import ar.edu.unlam.mobile.scaffolding.data.repositories.IngredientsRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.IngredientsRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.RecipesRepositoryImpl
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepository
import ar.edu.unlam.mobile.scaffolding.data.repositories.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Instala las dependencias en el contenedor de nivel de aplicación
abstract class RepositoryModule {
    @Binds
    @Singleton // Asegura que la vinculación también sea singleton (opcional si la clase ya lo es)
    abstract fun bindRecipesRepository(
        impl: RecipesRepositoryImpl, // Hilt sabrá cómo crear RecipesRepositoryImpl gracias a @Inject constructor
    ): RecipesRepository

    @Binds
    @Singleton
    abstract fun bindIngredientsRepository(impl: IngredientsRepositoryImpl): IngredientsRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DispatcherModule {
    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
