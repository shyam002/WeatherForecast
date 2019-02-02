package com.example.administrator.weatherforecast

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.example.administrator.starkspire.adapter.WeatherAdapter
import com.example.administrator.weatherforecast.jsonclass.Forecastday
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {

    val TAG = MainActivity::class.java.simpleName
    val PermissionsRequestCode = 123
    val daysNo = 4
    var tempcTV: TextView? = null
    var conditiontextTV: TextView? = null
    var cityNameTV: TextView? = null
    var regiontextTV: TextView? = null
    var countrytextTV: TextView? = null
    lateinit var progressBar: ProgressBar
    lateinit var imagview:ImageView
    lateinit var context: Context
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    protected var mLastLocation: Location? = null
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        startWeatherForecasting()
    }

    fun initView() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        tempcTV = findViewById(R.id.temp_c)
        conditiontextTV = findViewById(R.id.conditiontext)
        cityNameTV = findViewById(R.id.cityName)
        countrytextTV = findViewById(R.id.countrytext)
        regiontextTV = findViewById(R.id.regiontext)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressbar)
        imagview = findViewById(R.id.imagview)
        context = this
    }

    fun startWeatherForecasting(){
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
    internal fun setApplicationListViewType(forecastList: List<Forecastday>) {
        try {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.setLayoutManager(LinearLayoutManager(this))
            val weatherAdapter = WeatherAdapter(this, forecastList)
            recyclerView.setItemAnimator(DefaultItemAnimator())
            recyclerView.setAdapter(weatherAdapter)
        } catch (e: Exception) {
            e.printStackTrace()
        }
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

                    callWeatherForecast(mLastLocation!!.latitude, mLastLocation!!.longitude)
                    println("(mLastLocation)!!.latitude=" + (mLastLocation)!!.latitude)
                    println("(mLastLocation)!!.longitude=" + (mLastLocation)!!.longitude)
                } else {
                    Log.w(TAG, "getLastLocation:exception", task.exception)

                }
            }
    }

    private fun callWeatherForecast(lat: Double, long: Double) {
        val apiService = ApixuWeatherApiService()
        val q = "$lat,$long"
        GlobalScope.launch(Dispatchers.Main) {
            val forecastResponse = apiService.getFutureWeather(q, daysNo).await()
            println("current=" + forecastResponse.toString())
            val current = forecastResponse.current
            tempcTV!!.text = "${current.tempC}\u2103"
            cityNameTV!!.text = "${forecastResponse.location.name}"
            conditiontextTV!!.text = "${current.condition.text}"
            regiontextTV!!.text = "${forecastResponse.location.region}"
            countrytextTV!!.text = "${forecastResponse.location.country}"
            Picasso.with(context).load("https:${current.condition.icon}").into(imagview)
            setApplicationListViewType(forecastResponse.forecast.forecastday)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.refreshMenu) {
            progressBar.visibility = View.VISIBLE
            startWeatherForecasting()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
