package com.mpazpro3.app.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ENTITY: tabla "alternativas". Cada fila es una opción de respuesta de una actividad.
 *
 * Relación: muchas alternativas pertenecen a UNA actividad (N:1).
 * La ForeignKey asegura que no exista una alternativa de una actividad que no existe.
 */
@Entity(
    tableName = "alternativas",
    foreignKeys = [
        ForeignKey(
            entity = ActividadEntity::class,
            parentColumns = ["id"],          // columna de la tabla "actividades"
            childColumns = ["actividadId"],  // columna de esta tabla
            onDelete = ForeignKey.CASCADE    // si se borra la actividad, se borran sus alternativas
        )
    ],
    // Índice: acelera la búsqueda de alternativas por actividad (Room lo pide con ForeignKey).
    indices = [Index("actividadId")]
)
data class AlternativaEntity(
    // Autogenerado: SQLite asigna el número al insertar.
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val actividadId: String,
    val texto: String,
    val esCorrecta: Boolean,
    // Posición 0, 1, 2... Es el MISMO índice que la pantalla envía al responder.
    val posicion: Int
)
