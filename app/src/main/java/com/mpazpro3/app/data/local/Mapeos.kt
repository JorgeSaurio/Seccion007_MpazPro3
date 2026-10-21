package com.mpazpro3.app.data.local

import com.mpazpro3.app.model.Actividad
import com.mpazpro3.app.model.Alternativa
import com.mpazpro3.app.model.Dificultad

/**
 * Conversores entre las clases de la base de datos (Entity) y las clases del modelo
 * (model/), que son las que usan el ViewModel y las pantallas.
 *
 * Así las pantallas NO saben que existe Room: siguen trabajando con Actividad y Alternativa.
 */

/** Base de datos → modelo. Ordena las alternativas por posición para respetar el índice. */
fun ActividadConAlternativas.aModelo(): Actividad = Actividad(
    id = actividad.id,
    contenidoId = actividad.contenidoId,
    enunciado = actividad.enunciado,
    alternativas = alternativas
        .sortedBy { it.posicion }
        .map { Alternativa(texto = it.texto, esCorrecta = it.esCorrecta) },
    dificultad = Dificultad.valueOf(actividad.dificultad),   // "FACIL" → Dificultad.FACIL
    orden = actividad.orden,
    esEvaluacionFinal = actividad.esEvaluacionFinal
)

/** Modelo → fila de la tabla "actividades" (sin alternativas). */
fun Actividad.aEntidad(): ActividadEntity = ActividadEntity(
    id = id,
    contenidoId = contenidoId,
    enunciado = enunciado,
    dificultad = dificultad.name,                            // Dificultad.FACIL → "FACIL"
    orden = orden,
    esEvaluacionFinal = esEvaluacionFinal
)

/** Modelo → filas de la tabla "alternativas". La posición es el índice en la lista. */
fun Actividad.alternativasEntidad(): List<AlternativaEntity> =
    alternativas.mapIndexed { indice, alternativa ->
        AlternativaEntity(
            actividadId = id,
            texto = alternativa.texto,
            esCorrecta = alternativa.esCorrecta,
            posicion = indice
        )
    }

