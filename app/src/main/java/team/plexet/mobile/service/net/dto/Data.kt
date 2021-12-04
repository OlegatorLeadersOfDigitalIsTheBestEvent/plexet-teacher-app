package team.plexet.mobile.service.net.dto

const val GPS_TYPE = "GPS_TYPE"
const val SENSOR_TYPE = "SENSOR_TYPE"

class Data(val type: String, val sensorData: SensorData?, val gpsData: GPSData?)