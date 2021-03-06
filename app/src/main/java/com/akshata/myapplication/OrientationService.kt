package com.akshata.myapplication

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData

private const val SENSOR_DELAY_MICROS = 8 * 1000 // 8ms

class OrientationService : LifecycleService(), SensorEventListener {

    companion object {
        val sensorData = MutableLiveData<FloatArray>()
    }

    private var sensorManager: SensorManager? = null
    private var rotationSensor: Sensor? = null

    private fun createSensorManager() {
        if (sensorManager == null) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            rotationSensor?.let {
                addRotationSensorListener()
            }
        }
    }

    private fun addRotationSensorListener() {
        sensorManager?.registerListener(
                this,
                rotationSensor,
                SENSOR_DELAY_MICROS
        )
    }

    private val myBinder: IMyAidlInterface.Stub = object : IMyAidlInterface.Stub() {
        override fun basicTypes(anInt: Int, aLong: Long, aBoolean: Boolean, aFloat: Float, aDouble: Double, aString: String?) {
            TODO("Not yet implemented")
        }

        override fun orientation(): String {
            createSensorManager()
            return sensorData.value?.contentToString() ?: "Press Fetch data button"
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return myBinder
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                sensorData.value = it.values
            }

        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}
