package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.RingtoneManager
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.exp
import kotlin.math.sin

object SoundManager {
    private var activeAudioTrack: AudioTrack? = null

    @Volatile
    private var isPlaying = false

    fun stopSound() {
        isPlaying = false
        try {
            activeAudioTrack?.stop()
            activeAudioTrack?.release()
            activeAudioTrack = null
        } catch (e: Exception) {
            // Safe ignore
        }
    }

    suspend fun playIslamicChime() = withContext(Dispatchers.Default) {
        if (isPlaying) {
            stopSound()
        }
        isPlaying = true

        val sampleRate = 44100
        val duration = 4.0 // seconds
        val numSamples = (duration * sampleRate).toInt()
        val generatedSnd = DoubleArray(numSamples)

        // Generate a beautiful, warm pentatonic scale chime sequence (F4, A4, Bb4, C5, F5)
        val freqs = doubleArrayOf(349.23, 440.00, 466.16, 523.25, 698.46)
        
        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            var sample = 0.0
            
            for (fIdx in freqs.indices) {
                val f = freqs[fIdx]
                val startTime = fIdx * 0.4 // staggered entries
                if (t >= startTime) {
                    val age = t - startTime
                    val envelope = exp(-age * 2.0) * sin(2.0 * Math.PI * f * age)
                    sample += envelope * 0.18
                }
            }
            
            val overallDecay = if (t > 3.0) (4.0 - t) else 1.0
            generatedSnd[i] = sample * overallDecay
        }

        val soundBuffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            var valSample = (generatedSnd[i] * 32767).toInt()
            if (valSample > 32767) valSample = 32767
            if (valSample < -32768) valSample = -32768
            soundBuffer[i] = valSample.toShort()
        }

        try {
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(numSamples * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            activeAudioTrack = audioTrack
            audioTrack.write(soundBuffer, 0, numSamples)
            audioTrack.play()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isPlaying = false
        }
    }

    fun playSystemAlert(context: Context) {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, notification)
            ringtone.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
