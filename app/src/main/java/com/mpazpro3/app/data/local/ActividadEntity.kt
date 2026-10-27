package com.mpazpro3.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTITY: tabla "actividades" (desafíos y preguntas de la evaluación final).
 *
 * Es la versión "para la base de datos" de model/Actividad.kt. La diferencia es que aquí NO
 * va la lista de alternativas: SQLite no guarda listas, así que las alternativas viven en su
 * propia tabla (AlternativaEntity) y se unen con el id de la actividad.
 */
@Entity(tableName = "actividades")
data class ActividadEntity(
    // Mismo id que en MockData ("A1", "A2", "E1"...). No es autogenerado.
    @PrimaryKey val id: String,
    // Contenido al que pertenece la actividad (ej. "C2" = Matemática).
    val contenidoId: String,
    val enunciado: String,
    // El enum Dificultad se guarda como texto: "FACIL", "MEDIO" o "DIFICIL".
    val dificultad: String,
    // Posición dentro de su misma dificultad.
    val orden: Int,
    // true = pregunta de la evaluación final (RF-09). false = desafío de práctica (RF-08).
    val esEvaluacionFinal: Boolean
)