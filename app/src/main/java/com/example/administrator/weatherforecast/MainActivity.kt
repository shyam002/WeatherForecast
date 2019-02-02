package com.example.administrator.weatherforecast

import android.content.Context
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName
    private val PermissionsRequestCode = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (setPermission()) {
            Log.v(TAG,"Already have permission")
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
                    Log.v(TAG,"permission granted")
                } else {
                    setPermission()
                }
            }
        }
    }


}
