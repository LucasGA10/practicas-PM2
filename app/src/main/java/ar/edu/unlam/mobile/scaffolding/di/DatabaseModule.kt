package ar.edu.unlam.mobile.scaffolding.di

import android.content.Context
import androidx.room.Room
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabaseCallback
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.InitialDataSource
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    @Provides
    fun provideInitialDataSource(): InitialDataSource {
        return InitialDataSource
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        // Provider para romper el ciclo de dependencia si el Callback necesita el DAO
        // y el DAO se provee usando la instancia de AppDatabase.
        recipeDaoProvider: Provider<RecipeDao>,
        applicationScope: CoroutineScope,
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "recipe_database",
        )
            .fallbackToDestructiveMigration(true) // Para desarrollo; en producción, usa migraciones reales.
            .addCallback(
                AppDatabaseCallback(
                    recipeDaoProvider = recipeDaoProvider, // Pasa el Provider
                    applicationScope = applicationScope,
                    initialDataSource = InitialDataSource, // Pasa tus datos
                ),
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
        return appDatabase.recipeDao()
    }
}
