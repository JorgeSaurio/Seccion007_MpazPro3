package com.mpazpro3.app

import com.mpazpro3.app.data.MockData
import com.mpazpro3.app.model.Actividad
import com.mpazpro3.app.model.Alternativa
import com.mpazpro3.app.model.Dificultad
import com.mpazpro3.app.model.esRespuestaCorrecta
import com.mpazpro3.app.model.filtrarYOrdenarDesafios
import com.mpazpro3.app.model.siguienteNumeroIntento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pruebas de las reglas del RF-08. Cada test corresponde a un criterio de aceptación o a un
 * caso alternativo del documento de requerimientos.
 *
 * Ejecutar: clic derecho sobre esta clase > Run 'ReglasDesafioTest'.
 */
class ReglasDesafioTest {

    private val actividades = MockData.actividades
    private val a1 = actividades.first { it.id == "A1" }   // "¿Cuántas centenas tiene 300?"

    // Criterio 1: C2 muestra A1, A2, A3 en ese orden.
    @Test
    fun c2_muestraLosTresDesafiosEnOrden() {
        val ids = filtrarYOrdenarDesafios(actividades, "C2").map { it.id }
        assertEquals(listOf("A1", "A2", "A3"), ids)
    }

    // Criterio 1: E1 y E2 (evaluación final) no aparecen.
    @Test
    fun c2_noIncluyeLaEvaluacionFinal() {
        val desafios = filtrarYOrdenarDesafios(actividades, "C2")
        assertTrue(desafios.none { it.esEvaluacionFinal })
    }

    // Regla: la dificultad manda sobre "orden" (nunca un DIFICIL antes de un FACIL).
    @Test
    fun ordenaPorDificultadAntesQuePorOrden() {
        val alternativas = listOf(Alternativa("x", true))
        val desordenadas = listOf(
            Actividad("D", "CX", "difícil", alternativas, Dificultad.DIFICIL, orden = 1),
            Actividad("M", "CX", "medio", alternativas, Dificultad.MEDIO, orden = 1),
            Actividad("F2", "CX", "fácil 2", alternativas, Dificultad.FACIL, orden = 2),
            Actividad("F1", "CX", "fácil 1", alternativas, Dificultad.FACIL, orden = 1)
        )
        val ids = filtrarYOrdenarDesafios(desordenadas, "CX").map { it.id }
        assertEquals(listOf("F1", "F2", "M", "D"), ids)
    }

    // Caso alternativo: contenido sin desafíos o sin contenido seleccionado.
    @Test
    fun contenidoSinDesafios_devuelveListaVacia() {
        assertTrue(filtrarYOrdenarDesafios(actividades, "C99").isEmpty())
        assertTrue(filtrarYOrdenarDesafios(actividades, null).isEmpty())
    }

    // Criterio 2: en A1, elegir "3" (posición 0) es correcto.
    @Test
    fun a1_elegir3_esCorrecta() {
        assertEquals(true, esRespuestaCorrecta(a1, 0))
    }

    // Criterio 3: en A1, elegir "30" (posición 1) es incorrecto.
    @Test
    fun a1_elegir30_esIncorrecta() {
        assertEquals(false, esRespuestaCorrecta(a1, 1))
    }

    // Caso alternativo: índice fuera de rango → null (no se registra).
    @Test
    fun indiceFueraDeRango_devuelveNull() {
        assertNull(esRespuestaCorrecta(a1, 5))
        assertNull(esRespuestaCorrecta(a1, -1))
    }

    // Reintentos: el número de intento sube de a 1.
    @Test
    fun numeroDeIntento_sumaUno() {
        assertEquals(1, siguienteNumeroIntento(0))
        assertEquals(3, siguienteNumeroIntento(2))
    }
}