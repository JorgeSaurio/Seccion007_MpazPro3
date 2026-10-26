package com.mpazpro3.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * DAO: las consultas que necesita el RF-08. Room escribe la implementación al compilar (KSP).
 *
 * Regla (igual que en la demo Mi Biblioteca):
 *  - Lo que la app "observa" devuelve Flow (sin suspend): Room avisa cada vez que cambia.
 *  - Escrituras y lecturas puntuales son suspend: no pueden correr en el hilo principal.
 */
@Dao
interface DesafioDao {

    // ---------- Actividades ----------

    // @Transaction: Room hace 2 consultas (actividades + alternativas) y las junta;
    // con @Transaction ambas se leen como una sola operación.
    @Transaction
    @Query("SELECT * FROM actividades")
    fun actividadesConAlternativas(): Flow<List<ActividadConAlternativas>>

    // Para saber si la base está vacía (primera vez que se abre la app).
    @Query("SELECT COUNT(*) FROM actividades")
    suspend fun contarActividades(): Int

    // IGNORE: si la fila ya existe, no hace nada (evita errores si se llama dos veces).
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarActividades(actividades: List<ActividadEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarAlternativas(alternativas: List<AlternativaEntity>)

    // ---------- Respuestas ----------

    // Guarda una respuesta. Devuelve el id generado.
    @Insert
    suspend fun insertarRespuesta(respuesta: RespuestaEntity): Long

    // Cuántas veces el estudiante ya respondió ese desafío (para calcular el próximo intento).
    @Query(
        "SELECT COUNT(*) FROM respuestas " +
                "WHERE estudianteId = :estudianteId AND actividadId = :actividadId"
    )
    suspend fun contarIntentos(estudianteId: String, actividadId: String): Int

    // ---------- Seguimientos ----------

    // Devuelve null si el estudiante todavía no tiene seguimiento en ese contenido.
    @Query(
        "SELECT * FROM seguimientos " +
                "WHERE estudianteId = :estudianteId AND contenidoId = :contenidoId"
    )
    suspend fun obtenerSeguimiento(estudianteId: String, contenidoId: String): SeguimientoEntity?

    // REPLACE: si ya existe (misma clave estudianteId + contenidoId) lo reemplaza;
    // si no existe, lo crea. Sirve para "crear o actualizar".
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSeguimiento(seguimiento: SeguimientoEntity)
}