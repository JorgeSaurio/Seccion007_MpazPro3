package com.mpazpro3.app.ui.screen.estudiante

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.mpazpro3.app.navigation.Rutas
import com.mpazpro3.app.ui.components.BarraSuperior
import com.mpazpro3.app.ui.components.vibrarRespuesta
import com.mpazpro3.app.viewmodel.MpazProViewModel

/** Verde para "¡Correcto!" (el tema no trae un color de éxito). */
private val VerdeCorrecto = Color(0xFF2E7D32)

/**
 * RF-08: desafíos de práctica, uno a la vez y de Fácil a Difícil, antes de la evaluación final.
 *
 * La pantalla solo dibuja y llama al ViewModel (MVVM): el filtro, el orden y el registro de
 * respuestas están en MpazProViewModel (desafiosDe, responderDesafio).
 *
 * Mejoras del RF-08 sobre la plantilla:
 *  - Retroalimentación animada (aparece con fade + escala) en verde o rojo.
 *  - Vibración al responder (recurso nativo).
 *  - Diálogo "¡Terminaste los desafíos!" al responder el último.
 *  - Reintentos: se puede volver a tocar una alternativa; cada toque se guarda.
 */
@Composable
fun DesafiosScreen(navController: NavHostController, viewModel: MpazProViewModel) {
    val contenidoId by viewModel.contenidoSeleccionadoId.collectAsState()
    val usuario by viewModel.usuarioActivo.collectAsState()
    val desafios = remember(contenidoId) { viewModel.desafiosDe(contenidoId) }
    val context = LocalContext.current

    var indice by remember { mutableIntStateOf(0) }
    var resultado by remember { mutableStateOf<Boolean?>(null) }
    var respondido by remember { mutableStateOf(false) }
    // El diálogo final se muestra una sola vez por visita a la pantalla.
    var mostrarFin by remember { mutableStateOf(false) }
    var finYaMostrado by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraSuperior("Desafíos", navController) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (desafios.isEmpty()) {
                Text("No hay desafíos para este contenido.", style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            val actual = desafios[indice.coerceIn(0, desafios.lastIndex)]

            Text("Desafío ${indice + 1} de ${desafios.size} · ${actual.dificultad.etiqueta}", style = MaterialTheme.typography.labelLarge)
            Text(actual.enunciado, style = MaterialTheme.typography.headlineSmall)

            actual.alternativas.forEachIndexed { posicion, alternativa ->
                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    onClick = {
                        val respuesta = viewModel.responderDesafio(usuario?.id.orEmpty(), actual, posicion)
                        resultado = respuesta
                        respondido = true

                        if (respuesta != null) {
                            vibrarRespuesta(context, respuesta)
                            // Último desafío respondido → confirmar que terminó (una vez).
                            if (indice == desafios.lastIndex && !finYaMostrado) {
                                mostrarFin = true
                                finYaMostrado = true
                            }
                        }
                    }
                ) { Text(alternativa.texto, style = MaterialTheme.typography.titleMedium) }
            }

            // Animación: el mensaje aparece con fade + escala y desaparece con fade.
            AnimatedVisibility(
                visible = respondido,
                enter = fadeIn() + scaleIn(initialScale = 0.8f),
                exit = fadeOut()
            ) {
                val (texto, color) = when (resultado) {
                    true -> "¡Correcto!" to VerdeCorrecto
                    false -> "Inténtalo de nuevo." to MaterialTheme.colorScheme.error
                    null -> "No se pudo revisar la respuesta." to MaterialTheme.colorScheme.error
                }
                Text(texto, style = MaterialTheme.typography.titleLarge, color = color)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    enabled = indice > 0,
                    onClick = {
                        indice--
                        // Solo se oculta el mensaje; "resultado" se mantiene para que la
                        // animación de salida no muestre un texto distinto.
                        respondido = false
                    }
                ) { Text("Anterior") }
                Button(
                    modifier = Modifier.weight(1f),
                    enabled = indice < desafios.lastIndex,
                    onClick = {
                        indice++
                        respondido = false
                    }
                ) { Text("Siguiente") }
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = { navController.navigate(Rutas.ESTUDIANTE_EVALUACION) }
            ) { Text("Ir a la evaluación final", style = MaterialTheme.typography.titleMedium) }
        }

        // Diálogo de finalización (componente interactivo de Material 3).
        if (mostrarFin) {
            AlertDialog(
                onDismissRequest = { mostrarFin = false },
                title = { Text("¡Terminaste los desafíos!") },
                text = { Text("Puedes seguir practicando o ir a la evaluación final.") },
                confirmButton = {
                    TextButton(onClick = {
                        mostrarFin = false
                        navController.navigate(Rutas.ESTUDIANTE_EVALUACION)
                    }) { Text("Ir a la evaluación") }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarFin = false }) { Text("Seguir practicando") }
                }
            )
        }
    }
}