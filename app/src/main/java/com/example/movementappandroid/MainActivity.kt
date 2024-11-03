package com.example.movementappandroid

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import android.os.Bundle // Esta es la importación correcta
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var gyroscope: Sensor? = null
    private lateinit var statusTextView: TextView

    // Para reproducir sonido
    private lateinit var stableSound: MediaPlayer
    private lateinit var movementSound: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar la vista de texto para el estado
        statusTextView = findViewById(R.id.statusTextView)

        // Inicializar el SensorManager y los sensores
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Cargar los sonidos
        stableSound = MediaPlayer.create(this, R.raw.stable_sound)
        movementSound = MediaPlayer.create(this, R.raw.movement_sound)
    }

    override fun onResume() {
        super.onResume()
        accelerometer?.also { acc ->
            sensorManager.registerListener(this, acc, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.also { gyro ->
            sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event.values)
            Sensor.TYPE_GYROSCOPE -> handleGyroscope(event.values)
        }
    }

    private fun handleAccelerometer(values: FloatArray) {
        val x = values[0]
        val y = values[1]
        val z = values[2]

        // Calcular si el dispositivo está "Estable"
        val isStable = isDeviceStable(x, y, z)

        if (isStable) {
            statusTextView.text = "Estable"
            stableSound.start() // Reproduce sonido de estabilidad
        } else {
            statusTextView.text = "En Movimiento"
            movementSound.start() // Reproduce sonido de movimiento
        }
    }

    private fun handleGyroscope(values: FloatArray) {
        // Puedes implementar lógica adicional para el giroscopio si es necesario
    }

    private fun isDeviceStable(x: Float, y: Float, z: Float): Boolean {
        // Lógica para determinar si el dispositivo está estable
        // Aquí puedes establecer umbrales según las lecturas
        val threshold = 1.0f // Ajusta este valor según sea necesario
        return Math.abs(x) < threshold && Math.abs(y) < threshold && Math.abs(z - 9.81) < threshold
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No se necesita implementar en este caso
    }
}