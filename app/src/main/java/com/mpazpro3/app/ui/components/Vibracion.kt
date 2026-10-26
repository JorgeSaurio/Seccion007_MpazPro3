package com.mpazpro3.app.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Recurso nativo: vibración del dispositivo al responder un desafío.
 *   Correcta   → vibración corta.
 *   Incorrecta → vibración más larga.
 *
 * Es pertinente al caso (refuerzo inmediato para niños de 3° básico) y respeta sus
 * restricciones: no usa cámara, GPS, micrófono ni biometría. El permiso VIBRATE es "normal":
 * Android lo concede solo, sin preguntar al usuario.
 *
 * Android cambió la forma de pedir el vibrador con los años; por eso se revisa la versión
 * (la app funciona desde Android 7.0, API 24).
 */
fun vibrarRespuesta(context: Context, correcta: Boolean) {
    val vibrador: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12 o superior
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
            ?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    // Tablets o emuladores sin motor de vibración: no hacer nada.
    if (vibrador == null || !vibrador.hasVibrator()) return

    val duracionMs = if (correcta) 80L else 300L

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Android 8 o superior
        vibrador.vibrate(VibrationEffect.createOneShot(duracionMs, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrador.vibrate(duracionMs)
    }
}