package com.example.helloworld

import android.content.Intent
import android.graphics.Color
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.example.helloworld.ui.*
import com.example.helloworld.ui.FilamentTestActivity.Companion.BUNDLE_RENDER_TYPE
import com.google.android.filament.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "MainActivity"

        init {
            Utils.init()
        }
    }

    private lateinit var dialog: CustomDialog
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var rotationVectorSensor: Sensor? = null
    private var sensorAvailable: Boolean = false
    private val proximitySensorListener = object : SensorEventListener2 {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null && event.values[0] < proximitySensor!!.maximumRange) {
                // detect sth nearby
                window.decorView.setBackgroundColor(Color.RED)
            } else {
                window.decorView.setBackgroundColor(Color.WHITE)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        override fun onFlushCompleted(sensor: Sensor?) {}
    }

    private val gyroscopeSensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event != null) {
                when {
                    event.values[2] > 0.5f -> {
                        window.decorView.setBackgroundColor(Color.BLUE)
                    }
                    event.values[2] < -0.5f -> {
                        window.decorView.setBackgroundColor(Color.YELLOW)
                    }
                    else -> {
                        window.decorView.setBackgroundColor(Color.WHITE)
                    }
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    private val rvListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event?.values != null) {
                // 旋转矢量传感器结合了由陀螺仪，加速度计和磁力计生成的原始数据，以创建四元数 。 因此，其SensorEvent对象的values数组具有以下五个元素
                // 使用SensorManager类的getRotationMatrixFromVector()方法将四元数转换为旋转矩阵（4x4矩阵
                // 开发OpenGL应用程序，则可以直接使用旋转矩阵在3D场景中变换对象
                val rotationMatrix = FloatArray(16)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                // Remap coordinate system
                val remappedRotationMatrix = FloatArray(16)
                SensorManager.remapCoordinateSystem(
                    rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix
                )

                // 在调用getOrientation()方法之前，必须重新映射旋转矩阵的坐标系。 更准确地说，必须旋转旋转矩阵，以使新坐标系的Z轴与原始坐标系的Y轴重合
                // Convert to orientations
                val orientations = FloatArray(3)
                SensorManager.getOrientation(remappedRotationMatrix, orientations)

                // 默认情况下， orientations数组包含以弧度表示的角度而不是度数.以下代码将其所有角度转换为度
                for (i in 0 until 3) {
                    orientations[i] = Math.toDegrees(orientations[i].toDouble()).toFloat()
                }

                // z轴旋转角度
                if (orientations[2] > 45) {
                    window.decorView.setBackgroundColor(Color.YELLOW)
                } else if (orientations[2] < -45) {
                    window.decorView.setBackgroundColor(Color.BLUE)
                } else if (orientations[2] < 10) {
                    window.decorView.setBackgroundColor(Color.WHITE)
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data?.getStringExtra("data")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button_go_webview.setOnClickListener(this)
        button_go_filament.setOnClickListener(this)
        button_go_gltf.setOnClickListener(this)
        initDialog()
//        initSensor()
    }

    private fun initDialog() {
        dialog = CustomDialog(this, "Render Type").apply {
            initRadioGroup(
                RadioButtonContent(
                    FilamentFactory.RenderType.TRIANGLE,
                    R.id.render_type_triangle
                ),
                RadioButtonContent(
                    FilamentFactory.RenderType.RECT,
                    R.id.render_type_rect
                ),
                RadioButtonContent(
                    FilamentFactory.RenderType.IBL,
                    R.id.render_type_ibl
                ),
                RadioButtonContent(
                    FilamentFactory.RenderType.IBT,
                    R.id.render_type_ibt
                )
            )
            setConfirmListener {
                if (this.isChecked()) {
                    val renderType = getCheckedType()
                    val intent = Intent(this@MainActivity, FilamentTestActivity::class.java)
                    intent.putExtra(BUNDLE_RENDER_TYPE, renderType.text)
                    launcher.launch(intent)
                }
                dialog.dismiss()
            }
        }
    }

    private fun initSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) ?: return
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) ?: return
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: return
        sensorAvailable = true
        Log.d(TAG, "proximity sensor init success")
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_go_webview -> {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("url", "https://www.bilibili.com")
                launcher.launch(intent)
            }
            R.id.button_go_filament -> {
                dialog.show()
            }
            R.id.button_go_gltf -> {
                val intent = Intent(this, GLTFActivity::class.java)
                launcher.launch(intent)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (dialog.isShowing) {
            dialog.dismiss()
        }

        if (sensorAvailable) {
            sensorManager.unregisterListener(proximitySensorListener, proximitySensor)
            sensorManager.unregisterListener(gyroscopeSensorListener, gyroscopeSensor)
            sensorManager.unregisterListener(rvListener, rotationVectorSensor)
        }
    }

    override fun onResume() {
        super.onResume()
        if (sensorAvailable) {
            sensorManager.registerListener(
                proximitySensorListener,
                proximitySensor,
                2 * 1000 * 1000
            )
            sensorManager.registerListener(
                gyroscopeSensorListener,
                gyroscopeSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            sensorManager.registerListener(
                rvListener,
                rotationVectorSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }
}