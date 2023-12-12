package com.crepass.kotlin_weather

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.media.audiofx.Equalizer.Settings
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.crepass.kotlin_weather.databinding.ActivityMainBinding
import com.crepass.kotlin_weather.databinding.ItemForecastBinding
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationServices.*
import fastcampus.part2.chapter7.ForecastEntity
import fastcampus.part2.chapter7.WeatherEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {

            permissions.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                updateLocation()
            }

            else -> {
                //교육용 팝업 띄우는 곳..?
                Toast.makeText(this, "위치권한이 필요", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )


    }



    private fun updateLocation() {
        val fusedLocationClient = getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionRequest.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION))
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {

            Thread{
                try{
                    val addressList=Geocoder(this, Locale.KOREA).getFromLocation(
                        it.latitude,
                        it.longitude,
                        1
                    )
                    runOnUiThread{
                        binding.locationTextView.text=addressList?.get(0)?.thoroughfare.orEmpty()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                }


            }.start()

            WeatherRepository.getVillaageForecast(
                longitude = it.longitude,
                latitude = it.latitude,
                successCallback = {list->
                    val currentForecast=list.first()
                    binding.temperatureTextView.text =
                        getString(R.string.temperature_text, currentForecast.temperature)
                    binding.skyTextView.text = currentForecast.weather
                    binding.precipitationTextView.text =
                        getString(R.string.precipitation_text, currentForecast.precipitation)
                    binding.childForecastLayout.apply {
                        list.forEachIndexed { index, forecast ->
                            if (index == 0) {
                                return@forEachIndexed
                            }

                            val itemView = ItemForecastBinding.inflate(layoutInflater)

                            itemView.timeTextView.text = forecast.forecastTime
                            itemView.weatherTextView.text = forecast.weather
                            itemView.temperatureTextView.text =
                                getString(R.string.temperature_text, forecast.temperature)

                            addView(itemView.root)
                        }
                    }
                    Log.e("Forecast",list.toString())
                },
                failureCallback = {
                    it.printStackTrace()
                }
            )


        }


    }


}