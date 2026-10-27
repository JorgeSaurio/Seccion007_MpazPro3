package com.mpazpro3.app.model

/**
 * Reglas del RF-08 como funciones PURAS: reciben datos y devuelven un resultado, sin Room,
 * sin pantallas y sin Android. Por eso se pueden probar con tests unitarios
 * (ver ReglasDesafioTest), igual que ValidacionLibro en la demo Mi Biblioteca.
 *
 * Las usan el ViewModel (desafiosDe, responderDesafio) y el Repository (número de intento).
 */

/**
 * Desafíos de un contenido, SIN la evaluación final, en dificultad progresiva:
 * primero por dificultad (FACIL → MEDIO → DIFICIL) y, dentro de cada una, por "orden".
 *
 * dificultad.ordinal es la posición en el enum Dificultad: FACIL = 0, MEDIO = 1,
 * DIFICIL = 2. Si alguien cambia el orden del enum, cambia el orden de los desafíos.
 */
fun filtrarYOrdenarDesafios(actividades: List<Actividad>, contenidoId: String?): List<Actividad> =
    actividades
        .filter { it.contenidoId == contenidoId && !it.esEvaluacionFinal }
        .sortedWith(compareBy<Actividad>({ it.dificultad.ordinal }, { it.orden }))

/**
 * ¿La alternativa elegida es la correcta?
 *   true  = correcta
 *   false = incorrecta
 *   null  = el índice no existe (ej. 5 cuando hay 3 alternativas). No se debe registrar.
 */
fun esRespuestaCorrecta(actividad: Actividad, indiceAlternativa: Int): Boolean? =
    actividad.alternativas.getOrNull(indiceAlternativa)?.esCorrecta

/**
 * Reintentos permitidos: el próximo intento es el número de respuestas previas + 1.
 * Ej.: si ya respondió 2 veces ese desafío, este es el intento 3.
 */
fun siguienteNumeroIntento(intentosPrevios: Int): Int = intentosPrevios + 1
