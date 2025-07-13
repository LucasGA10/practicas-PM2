package ar.edu.unlam.mobile.scaffolding.di

import android.content.Context
import androidx.room.Room
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabase
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabaseCallback
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.IngredientDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.RecipeDao
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.dao.UsedIngredientDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    // Provisión del AppDatabaseCallback
    @Provides
    @Singleton
    fun provideAppDatabaseCallback(
        @ApplicationContext context: Context,
        // Usar Provider<DAO> para evitar dependencia circular y asegurar
        // que los DAOs se obtengan cuando la BD esté completamente lista.
        // Esto es crucial para que el callback funcione correctamente.
        recipeDaoProvider: Provider<RecipeDao>,
        ingredientDaoProvider: Provider<IngredientDao>,
        usedIngredientDaoProvider: Provider<UsedIngredientDao>,
    ): AppDatabaseCallback {
        // El callback ahora recibe Providers para los DAOs
        return AppDatabaseCallback(context, recipeDaoProvider, ingredientDaoProvider, usedIngredientDaoProvider)
    }

    // Provisión de AppDatabase
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        // Hilt inyectará la instancia de AppDatabaseCallback que creó usando provideAppDatabaseCallback()
        callback: AppDatabaseCallback,
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "app_database_name",
        )
            .fallbackToDestructiveMigration(true) // Considera migraciones reales para producción
            .addCallback(callback)
            .build()
    }

    @Provides
    @Singleton
    fun provideRecipeDao(appDatabase: AppDatabase): RecipeDao {
        return appDatabase.recipeDao()
    }

    @Provides
    @Singleton
    fun provideIngredientDao(appDatabase: AppDatabase): IngredientDao {
        return appDatabase.ingredientDao()
    }

    @Provides
    @Singleton
    fun provideUsedIngredientDao(appDatabase: AppDatabase): UsedIngredientDao {
        return appDatabase.usedIngredientDao()
    }
}
