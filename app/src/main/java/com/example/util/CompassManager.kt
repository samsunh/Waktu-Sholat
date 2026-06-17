package com.example.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class CompassManager(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    fun getHeadingFlow(): Flow<Float> = callbackFlow {
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        // Keep track of acceleration and magnetic readings if rotation vector is unavailable
        var gravityValues = FloatArray(3)
        var geomagneticValues = FloatArray(3)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return

                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuthInRadians = orientationAngles[0]
                    var azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                    azimuthInDegrees = (azimuthInDegrees + 360) % 360
                    trySend(azimuthInDegrees)
                } else {
                    if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                        gravityValues = event.values.clone()
                    } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                        geomagneticValues = event.values.clone()
                    }

                    if (gravityValues.isNotEmpty() && geomagneticValues.isNotEmpty()) {
                        val r = FloatArray(9)
                        val i = FloatArray(9)
                        if (SensorManager.getRotationMatrix(r, i, gravityValues, geomagneticValues)) {
                            SensorManager.getOrientation(r, orientationAngles)
                            val azimuthInRadians = orientationAngles[0]
                            var azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
                            azimuthInDegrees = (azimuthInDegrees + 360) % 360
                            trySend(azimuthInDegrees)
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }

    companion object {
        fun calculateQiblaDirection(latUser: Double, lonUser: Double): Double {
            val lat1 = Math.toRadians(latUser)
            val lon1 = Math.toRadians(lonUser)
            // Kaaba coordinates (latitude: 21.422478 N, longitude: 39.826214 E)
            val lat2 = Math.toRadians(21.422478)
            val lon2 = Math.toRadians(39.826214)

            val dLon = lon2 - lon1

            val y = sin(dLon) * cos(lat2)
            val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

            var bearing = Math.toDegrees(atan2(y, x))
            bearing = (bearing + 360) % 360
            return bearing
        }
    }
}
