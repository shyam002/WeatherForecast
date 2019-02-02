package com.example.administrator.weatherforecast

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName
    val PermissionsRequestCode = 123
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (setPermission()) {
            Log.v(TAG, "Already have permission")
            getLastLocation()
        }
    }

    fun setPermission(): Boolean {
        val askRunTimePermission = AskRunTimePermission(this)
        val perlist = ArrayList<String>()
        perlist.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        perlist.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        return askRunTimePermission.setPermissions(perlist, PermissionsRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        println("MainActivity onRequestPermissionsResult")
        val granted = grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
        when (requestCode) {
            PermissionsRequestCode -> {
                if (granted) {
                    Log.v(TAG, "permission granted")
                    getLastLocation()
                } else {
                    setPermission()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mFusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    mLastLocation = task.result

                    callWeatherForecast(mLastLocation!!.latitude,mLastLocation!!.longitude)
                    println("(mLastLocation)!!.latitude="+(mLastLocation)!!.latitude)
                    println("(mLastLocation)!!.longitude="+(mLastLocation)!!.longitude)
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)

                }
            }
    }

    private fun callWeatherForecast(lat:Double,long:Double) {
        val apiService = ApixuWeatherApiService()
        val q = "$lat,$long"
        GlobalScope.launch(Dispatchers.Main) {
            val forecastResponse = apiService.getFutureWeather(q,4).await()
            println("current="+forecastResponse.toString())
            println("current="+forecastResponse.current.toString())
        }
    }

}
