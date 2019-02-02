package com.example.administrator.weatherforecast

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

import java.util.ArrayList

class AskRunTimePermission(internal var activity: Activity) {
    internal lateinit var permissionarray: Array<String>

    fun setPermissions(perlist: ArrayList<String>, requestcode: Int): Boolean {
        println("call checkLocationPermissionAndEnableGps")
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                var shouldRequestPermissionWithMsg = false
                var msg = activity.resources.getString(R.string.permssionmsg)
                for (i in perlist.indices) {
                    val permission = perlist[i]
                    if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                            shouldRequestPermissionWithMsg = true
                            if (!msg.isEmpty()) msg += "\n"
                            msg += activity.resources.getString(R.string.bullet) + permission.replace(
                                "android.permission.",
                                " "
                            )
                        }
                    }else{
                        return true
                    }
                }
                if (!perlist.isEmpty()) {
                    permissionarray = perlist.toTypedArray()
                    if (shouldRequestPermissionWithMsg) {
                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        val alertDialogBuilder = AlertDialog.Builder(activity)
                        alertDialogBuilder.setTitle("")
                        alertDialogBuilder
                            .setMessage(msg)
                            .setCancelable(false)
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.cancel()
                                ActivityCompat.requestPermissions(
                                    activity,
                                    permissionarray,
                                    requestcode
                                )
                            }
                            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                        val alertDialog = alertDialogBuilder.create()
                        alertDialog.show()
                    } else {

                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(
                            activity,
                            permissionarray,
                            requestcode
                        )

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int  The callback method gets the
                        // result of the request.
                    }
                } else {
                    return true
                }
            } else {
                return true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return false
    }
}
