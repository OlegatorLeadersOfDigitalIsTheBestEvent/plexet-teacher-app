package team.plexet.mobile.service.foreground

import android.Manifest
import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.Sensor.*
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import team.plexet.mobile.models.SensorDataModel
import team.plexet.mobile.service.location.LocationService
import team.plexet.mobile.service.net.RetrofitService
import team.plexet.mobile.service.net.dto.DataDTO
import team.plexet.mobile.settings.getName
import team.plexet.mobile.settings.getPeriod
import team.plexet.mobile.settings.getTrackNum
import java.time.Instant
import java.util.*

class ForegroundService : Service(), SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var deviceSensors: List<Sensor>? = null
    private val valuesMap: HashMap<Int, SensorDataModel?> = HashMap()
    private var sending = false
    private val locationService = LocationService()
    private var timer: Timer? = null
    private var location: Location? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val builder = Notification.Builder(this)
            .setSmallIcon(R.drawable.ic_secure)
            .setPriority(Notification.PRIORITY_LOW)
        val notification = builder.build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(
                "ForegroundService",
                "ForegroundService",
                NotificationManager.IMPORTANCE_LOW
            )
            nm.createNotificationChannel(nc)
            builder.setChannelId("ForegroundService")
        }
        startForeground(777, notification)
        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        deviceSensors = mSensorManager!!.getSensorList(Sensor.TYPE_ALL)
        startTimer()
        for (value in deviceSensors!!)
            mSensorManager?.registerListener(this, value, SensorManager.SENSOR_DELAY_FASTEST)
        locationService.setContext(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (sending) {
            location?.let { send(it) }
            sending = false
        } else {
            if (event == null)
                return
            valuesMap[event.sensor.type] = SensorDataModel(event.timestamp, event.values)
        }
    }

    private fun send(location: Location) {
        val call = RetrofitService.instance.postData(getSensorDTO(valuesMap, location))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                println("response: " + (response.body()?.string() ?: "error"))
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("response bad")
            }
        })
    }

    private fun getSensorDTO(values: HashMap<Int, SensorDataModel?>, location: Location): DataDTO {
        val dataDTO = DataDTO()
        val accData = values[TYPE_ACCELEROMETER]?.values
        if (accData != null) {
            //ax, ay, az
            dataDTO.ax = accData[0]
            dataDTO.ay = accData[1]
            dataDTO.az = accData[2]
        } else {
            //ax, ay, az
            dataDTO.ax = 0f
            dataDTO.ay = 0f
            dataDTO.az = 0f
        }
        val gyrData = values[TYPE_GYROSCOPE]?.values
        if (gyrData != null) {
            //wx, wy, wz
            dataDTO.wx = gyrData[0]
            dataDTO.wy = gyrData[1]
            dataDTO.wz = gyrData[2]
        } else {
            //wx, wy, wz
            dataDTO.wx = 0f
            dataDTO.wy = 0f
            dataDTO.wz = 0f
        }
        val magnData = values[TYPE_MAGNETIC_FIELD]?.values
        if (magnData != null) {
            //mx, my, mz
            dataDTO.mx = magnData[0]
            dataDTO.my = magnData[1]
            dataDTO.mz = magnData[2]
        } else {
            //mx, my, mz
            dataDTO.mx = 0f
            dataDTO.my = 0f
            dataDTO.mz = 0f
        }
        val magnUncalcData = values[TYPE_MAGNETIC_FIELD_UNCALIBRATED]?.values
        if (magnUncalcData != null) {
            //mx0, my0, mz0, dmx, dmy, dmz
            dataDTO.mx0 = magnUncalcData[0]
            dataDTO.my0 = magnUncalcData[1]
            dataDTO.mz0 = magnUncalcData[2]
            dataDTO.dmx = magnUncalcData[3]
            dataDTO.dmy = magnUncalcData[4]
            dataDTO.dmz = magnUncalcData[5]
        } else {
            //mx0, my0, mz0, dmx, dmy, dmz
            dataDTO.mx0 = 0f
            dataDTO.my0 = 0f
            dataDTO.mz0 = 0f
            dataDTO.dmx = 0f
            dataDTO.dmy = 0f
            dataDTO.dmz = 0f
        }
        val gyrUncalcData = values[TYPE_GYROSCOPE_UNCALIBRATED]?.values
        if (gyrUncalcData != null) {
            //wx0, wy0, wz0, dwx, dwy, dwz
            dataDTO.wx0 = gyrUncalcData[0]
            dataDTO.wy0 = gyrUncalcData[1]
            dataDTO.wz0 = gyrUncalcData[2]
            dataDTO.dwx = gyrUncalcData[3]
            dataDTO.dwy = gyrUncalcData[4]
            dataDTO.dwz = gyrUncalcData[5]
        } else {
            //wx0, wy0, wz0, dwx, dwy, dwz
            dataDTO.wx0 = 0f
            dataDTO.wy0 = 0f
            dataDTO.wz0 = 0f
            dataDTO.dwx = 0f
            dataDTO.dwy = 0f
            dataDTO.dwz = 0f
        }
        val accUncalcData = values[TYPE_ACCELEROMETER_UNCALIBRATED]?.values
        if (accUncalcData != null) {
            //ax0, ay0, az0, dax, day, daz
            dataDTO.ax0 = accUncalcData[0]
            dataDTO.ay0 = accUncalcData[1]
            dataDTO.az0 = accUncalcData[2]
            dataDTO.dax = accUncalcData[3]
            dataDTO.day = accUncalcData[4]
            dataDTO.daz = accUncalcData[5]
        } else {
            //ax0, ay0, az0, dax, day, daz
            dataDTO.ax0 = 0f
            dataDTO.ay0 = 0f
            dataDTO.az0 = 0f
            dataDTO.dax = 0f
            dataDTO.day = 0f
            dataDTO.daz = 0f
        }


        dataDTO.name = getName(this)
        dataDTO.dt = getPeriod(this)
        dataDTO.N = getTrackNum(this)

        dataDTO.tcnt = Instant.now().toEpochMilli()
        dataDTO.long = location.longitude
        dataDTO.atti = location.latitude
        return dataDTO
    }

    private fun floatArrayToString(values: FloatArray): String {
        var result = "{"
        for (value in values) {
            result += "$value, "
        }
        return "$result}"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun startTimer() {
        timer?.cancel()
        timer = Timer()
        val context = this
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationClient.getCurrentLocation(
                    LocationRequest.PRIORITY_HIGH_ACCURACY, null
                ).addOnSuccessListener {
                    location = it
                    sending = true
                }
            }
        }, getPeriod(this), getPeriod(this))
    }
}