package com.mpazpro3.app.data.repository

import androidx.room.withTransaction
import com.mpazpro3.app.data.local.AppDatabase
import com.mpazpro3.app.data.local.DatosIniciales
import com.mpazpro3.app.data.local.RespuestaEntity
import com.mpazpro3.app.data.local.SeguimientoEntity
import com.mpazpro3.app.data.local.aModelo
import com.mpazpro3.app.model.Actividad
import com.mpazpro3.app.model.EstadoAvance
import com.mpazpro3.app.model.siguienteNumeroIntento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * REPOSITORY del RF-08: la única puerta a los datos de desafíos para el ViewModel.
 *
 * El ViewModel no sabe que existe Room: pide "actividades" o "registrar una respuesta" y el
 * repositorio decide cómo hacerlo. En la Experiencia 3 (con API) se cambia solo esta clase.
 */
class DesafioRepository(private val database: AppDatabase) {

    private val dao = database.desafioDao()

    /**
     * Todas las actividades (desafíos + evaluación final) ya convertidas al modelo.
     * Es un Flow: cada vez que la tabla cambia, emite la lista nueva.
     */
    val actividades: Flow<List<Actividad>> =
        dao.actividadesConAlternativas().map { lista -> lista.map { it.aModelo() } }

    /** La primera vez que se abre la app, copia las actividades de MockData a Room. */
    suspend fun cargarDatosIniciales() {
        DatosIniciales.cargarSiFalta(dao)
    }

    /**
     * Guarda una respuesta de desafío y deja el seguimiento en EN_PROGRESO.
     *
     * withTransaction: los 3 pasos se hacen como UNA operación. Si dos toques llegan muy
     * seguidos, no se repite el mismo número de intento.
     */
    suspend fun registrarRespuesta(
        estudianteId: String,
        actividad: Actividad,
        alternativa: Int,
        esCorrecta: Boolean
    ) {
        database.withTransaction {
            // 1) Número de intento: cuántas veces ya respondió este desafío + 1 (reintentos).
            val intentosPrevios = dao.contarIntentos(estudianteId, actividad.id)

            // 2) Guardar la respuesta.
            dao.insertarRespuesta(
                RespuestaEntity(
                    estudianteId = estudianteId,
                    actividadId = actividad.id,
                    alternativaElegida = alternativa,
                    esCorrecta = esCorrecta,
                    numeroIntento = siguienteNumeroIntento(intentosPrevios)
                )
            )

            // 3) Seguimiento: si no existe se crea; queda EN_PROGRESO.
            //    Si ya estaba FINALIZADO (rindió la evaluación), no se retrocede el estado.
            val seguimiento = dao.obtenerSeguimiento(estudianteId, actividad.contenidoId)
                ?: SeguimientoEntity(estudianteId = estudianteId, contenidoId = actividad.contenidoId)

            if (seguimiento.estado != EstadoAvance.FINALIZADO.name) {
                dao.guardarSeguimiento(seguimiento.copy(estado = EstadoAvance.EN_PROGRESO.name))
            }
        }
    }
}


