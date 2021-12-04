package team.plexet.mobile.service.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import team.plexet.mobile.service.net.RetrofitService
import team.plexet.mobile.service.net.dto.DataDTO
import java.util.function.Consumer

class LocationService {
    private var context: Context? = null

    fun setContext(context: Context?) {
        this.context = context
    }



}