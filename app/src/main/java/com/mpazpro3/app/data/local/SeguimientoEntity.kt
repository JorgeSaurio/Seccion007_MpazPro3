package com.mpazpro3.app.data.local

import androidx.room.Entity
import com.mpazpro3.app.model.EstadoAvance

/**
 * ENTITY: tabla "seguimientos". Una fila por estudiante y contenido.
 *
 * Clave primaria COMPUESTA (estudianteId + contenidoId): un estudiante tiene un solo
 * seguimiento por contenido. El RF-08 lo deja en EN_PROGRESO al responder un desafío.
 */
@Entity(
    tableName = "seguimientos",
    primaryKeys = ["estudianteId", "contenidoId"]
)
data class SeguimientoEntity(
    val estudianteId: String,
    val contenidoId: String,
    // Lo usa el RF-09 (evaluación final). El RF-08 no lo modifica.
    val intentosEvaluacionUsados: Int = 0,
    // Enum EstadoAvance guardado como texto: "NO_INICIADO", "EN_PROGRESO" o "FINALIZADO".
    val estado: String = EstadoAvance.NO_INICIADO.name
)