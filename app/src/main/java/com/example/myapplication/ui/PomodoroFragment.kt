package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.app.NotificationManager
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentPomodoroBinding
import com.google.android.material.snackbar.Snackbar

class PomodoroFragment : Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    private var timer: CountDownTimer? = null
    private var isRunning = false

    private var workTimeInMillis = 25 * 60 * 1000L // 25 minutos
    private var shortBreakInMillis = 5 * 60 * 1000L // 5 minutos
    private var longBreakInMillis = 15 * 60 * 1000L // 15 minutos
    private var currentMode = "WORK" // Modos: WORK, SHORT_BREAK, LONG_BREAK

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el temporizador en el modo de trabajo
        updateTimerDisplay(workTimeInMillis)
        binding.progressBar.progress = 100 // Progreso inicial al 100%

        // Inicializa el MediaPlayer con el sonido
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.timer_finish_sound)

        // Configurar botones
        binding.btnStartPause.setOnClickListener {
            if (isRunning) pauseTimer() else startTimer()
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        binding.btnSwitchMode.setOnClickListener {
            switchMode()
        }
    }

    private fun startTimer() {
        val timeInMillis = when (currentMode) {
            "WORK" -> {
                checkAndRequestDNDPermission()
                enableDoNotDisturb()
                workTimeInMillis
            }
            "SHORT_BREAK" -> shortBreakInMillis
            "LONG_BREAK" -> longBreakInMillis
            else -> workTimeInMillis
        }

        timer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                updateTimerDisplay(millisUntilFinished)

                // Actualiza el progreso circular
                val progress = ((millisUntilFinished.toFloat() / timeInMillis) * 100).toInt()
                binding.progressBar.progress = progress
            }

            override fun onFinish() {
                isRunning = false
                binding.btnStartPause.text = "Iniciar"
                playSoundAndVibrate() // Reproducir sonido y vibración
                Snackbar.make(binding.root, "¡Tiempo terminado!", Snackbar.LENGTH_LONG).show()

                // Desactiva el modo No Molestar si estaba activado
                disableDoNotDisturb()

                // Reinicia al siguiente modo automáticamente
                switchMode()
            }
        }.start()

        isRunning = true
        binding.btnStartPause.text = "Pausar"
    }

    private fun pauseTimer() {
        timer?.cancel()
        isRunning = false
        binding.btnStartPause.text = "Iniciar"
        disableDoNotDisturb() // Desactiva el modo No Molestar
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        when (currentMode) {
            "WORK" -> updateTimerDisplay(workTimeInMillis)
            "SHORT_BREAK" -> updateTimerDisplay(shortBreakInMillis)
            "LONG_BREAK" -> updateTimerDisplay(longBreakInMillis)
        }
        binding.btnStartPause.text = "Iniciar"
        binding.progressBar.progress = 100
        disableDoNotDisturb() // Desactiva el modo No Molestar
    }

    private fun switchMode() {
        resetTimer()
        currentMode = when (currentMode) {
            "WORK" -> {
                updateTimerDisplay(shortBreakInMillis)
                "SHORT_BREAK"
            }
            "SHORT_BREAK" -> {
                updateTimerDisplay(longBreakInMillis)
                "LONG_BREAK"
            }
            "LONG_BREAK" -> {
                updateTimerDisplay(workTimeInMillis)
                "WORK"
            }
            else -> "WORK"
        }

        val modeText = when (currentMode) {
            "WORK" -> "Modo Trabajo"
            "SHORT_BREAK" -> "Descanso Corto"
            "LONG_BREAK" -> "Descanso Largo"
            else -> "Modo Trabajo"
        }

        Snackbar.make(binding.root, "Cambiado a: $modeText", Snackbar.LENGTH_LONG).show()
    }

    private fun updateTimerDisplay(timeInMillis: Long) {
        val minutes = (timeInMillis / 1000) / 60
        val seconds = (timeInMillis / 1000) % 60
        binding.timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun playSoundAndVibrate() {
        // Reproducir sonido
        mediaPlayer?.start()

        // Activar vibración
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500) // Vibrar por 500ms para versiones antiguas
        }
    }

    // Verificar y solicitar permiso para el modo No Molestar
    private fun checkAndRequestDNDPermission() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            startActivity(intent)
        }
    }

    // Activar el modo No Molestar
    private fun enableDoNotDisturb() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
        }
    }

    // Desactivar el modo No Molestar
    private fun disableDoNotDisturb() {
        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (notificationManager.isNotificationPolicyAccessGranted) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        mediaPlayer?.release() // Libera los recursos del MediaPlayer
        mediaPlayer = null
        _binding = null
    }
}
