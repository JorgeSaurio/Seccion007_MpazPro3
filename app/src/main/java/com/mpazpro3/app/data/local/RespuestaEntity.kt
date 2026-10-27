package com.mpazpro3.app.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ENTITY: tabla "respuestas". Cada vez que el estudiante toca una alternativa en un desafío
 * se guarda UNA fila. Como se permiten reintentos, un mismo desafío puede tener varias filas
 * (numeroIntento 1, 2, 3...).
 */
@Entity(
    tableName = "respuestas",
    // Índice para contar rápido los intentos de un estudiante en una actividad.
    indices = [Index(value = ["estudianteId", "actividadId"])]
)
data class RespuestaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val estudianteId: String,      // ej. "EST-001" (dato ficticio)
    val actividadId: String,       // ej. "A1"
    val alternativaElegida: Int,   // posición de la alternativa tocada (0, 1, 2...)
    val esCorrecta: Boolean,
    val numeroIntento: Int,        // 1 = primer intento en ese desafío, 2 = segundo...
    // Fecha y hora en milisegundos. Se llena sola al crear el objeto.
    val fecha: Long = System.currentTimeMillis()
)
