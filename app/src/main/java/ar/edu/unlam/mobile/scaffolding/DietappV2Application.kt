package ar.edu.unlam.mobile.scaffolding

import android.app.Application
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import ar.edu.unlam.mobile.scaffolding.data.datasources.local.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class DietappV2Application : Application() {
    @Inject
    lateinit var appDatabaseProvider: Provider<AppDatabase>

    override fun onCreate() {
        super.onCreate()

        // Usar el lifecycleScope del proceso de la aplicación
        ProcessLifecycleOwner.get().lifecycleScope.launch(Dispatchers.IO) {
            Log.d("YourApplication", "Iniciando pre-población de la base de datos si es necesario...")
            try {
                // Acceder a la base de datos para forzar su creación e inicialización
                // AppDatabaseCallback.onCreate se ejecutará aquí si la BD es nueva.
                appDatabaseProvider.get().openHelper.writableDatabase
                Log.d("YourApplication", "La base de datos está lista/siendo poblada.")
            } catch (e: Exception) {
                Log.e("YourApplication", "Error durante la pre-población/inicialización de la BD.", e)
            }
        }
    }
}
