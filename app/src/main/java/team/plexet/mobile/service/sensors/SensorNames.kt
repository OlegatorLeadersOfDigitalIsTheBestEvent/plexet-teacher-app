package team.plexet.mobile.service.sensors

class SensorNames {
    companion object{
        val map: HashMap<Int, String> = HashMap()
        init {
            map[1] = "TYPE_ACCELEROMETER"
            map[35] = "TYPE_ACCELEROMETER_UNCALIBRATED"
            map[13] = "TYPE_AMBIENT_TEMPERATURE"
            map[15] = "TYPE_GAME_ROTATION_VECTOR"
            map[20] = "TYPE_GEOMAGNETIC_ROTATION_VECTOR"
            map[9] = "TYPE_GRAVITY"
            map[4] = "TYPE_GYROSCOPE"
            map[16] = "TYPE_GYROSCOPE_UNCALIBRATED"
            map[31] = "TYPE_HEART_BEAT"
            map[36] = "TYPE_HINGE_ANGLE"
            map[5] = "TYPE_LIGHT"
            map[10] = "TYPE_LINEAR_ACCELERATION"
            map[34] = "TYPE_LOW_LATENCY_OFFBODY_DETECT"
            map[2] = "TYPE_MAGNETIC_FIELD"
            map[14] = "TYPE_MAGNETIC_FIELD_UNCALIBRATED"
            map[30] = "TYPE_MOTION_DETECT"
            map[28] = "TYPE_POSE_6DOF"
            map[6] = "TYPE_PRESSURE"
            map[8] = "TYPE_PROXIMITY"
            map[12] = "TYPE_RELATIVE_HUMIDITY"
            map[11] = "TYPE_ROTATION_VECTOR"
            map[29] = "TYPE_STATIONARY_DETECT"
            map[19] = "TYPE_STEP_COUNTER"
            map[18] = "TYPE_STEP_DETECTOR"
            map[7] = "TYPE_TEMPERATURE"
        }
    }
}