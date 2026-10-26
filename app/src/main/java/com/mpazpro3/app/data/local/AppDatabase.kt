package com.mpazpro3.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mpazpro3.app.data.MockData

/**
 * DATABASE: une las 4 tablas con su DAO y entrega UNA sola instancia para toda la app
 * (patrón singleton, igual que en la demo).
 *
 * Si se cambia una entidad (agregar una columna, etc.), hay que subir "version".
 */
@Database(
    entities = [
        ActividadEntity::class,
        AlternativaEntity::class,
        RespuestaEntity::class,
        SeguimientoEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun desafioDao(): DesafioDao

    companion object {
        @Volatile
        private var instancia: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpazpro3.db"
                )
                    // Solo en desarrollo: si cambia el esquema, borra y recrea la base.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instancia = it }
            }
    }
}

/**
 * Carga inicial: la PRIMERA vez que se abre la app, copia las actividades de MockData a la
 * base de datos. Las siguientes veces la tabla ya tiene datos y no hace nada.
 *
 * Todos los datos son ficticios (RNF-04).
 */
object DatosIniciales {

    suspend fun cargarSiFalta(dao: DesafioDao) {
        if (dao.contarActividades() > 0) return

        // Primero las actividades y DESPUÉS las alternativas: la ForeignKey exige que la
        // actividad exista antes de insertar sus alternativas.
        dao.insertarActividades(MockData.actividades.map { it.aEntidad() })
        dao.insertarAlternativas(MockData.actividades.flatMap { it.alternativasEntidad() })
    }
}
