package team.plexet.mobile.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import team.plexet.mobile.R
import team.plexet.mobile.service.foreground.ForegroundService
import team.plexet.mobile.settings.*


class MainActivity : AppCompatActivity() {
    @BindView(R.id.etName)
    lateinit var etName: EditText
    @BindView(R.id.etPeriod)
    lateinit var etPeriod: EditText
    @BindView(R.id.etTrackNum)
    lateinit var etTrackNum: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        if (!isLocationAccessEnable(this))
            startActivity(Intent(this, LocationRequestActivity::class.java))
        else
            startService(Intent(this, ForegroundService::class.java))
        etName.setText(getName(this))
        etPeriod.setText(getPeriod(this).toString())
        etTrackNum.setText(getTrackNum(this).toString())
    }

    private fun isLocationAccessEnable(context: Context?): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        } else ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @OnClick(R.id.btName)
    fun onClickBtName(){
        setName(this, etName.text.toString())
    }

    @OnClick(R.id.btPriodTruckNum)
    fun onClickBtTruckNum(){
        setTrackNum(this,  etTrackNum.text.toString().toLong())
        setPeriod(this,  etPeriod.text.toString().toLong())
        startService(Intent(this, ForegroundService::class.java))
    }
}