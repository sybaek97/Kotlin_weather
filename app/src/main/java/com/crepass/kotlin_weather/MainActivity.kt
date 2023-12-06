package com.crepass.kotlin_weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fastcampus.part2.chapter7.ForecastEntity
import fastcampus.part2.chapter7.WeatherEntity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val retrofit = Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherService::class.java)
        val baseDateTime=BaseDateTime.getBaseDateTime()
        service.getVillageForecast(
            serviceKey = "Xljxf+UpwTCwNDWEEtKOUbhXAZ9bL+GPwBbsG2NxDZBlLIjOMJRyATe6492KBXNsB7cJIp1bLrv4m3Byq25fzw==",
            baseDate = baseDateTime.baseDate,
            baseTime = baseDateTime.baseTime,
            nx = 55,
            ny = 127
        ).enqueue(object : Callback<WeatherEntity> {
            override fun onResponse(call: Call<WeatherEntity>, response: Response<WeatherEntity>) {

                val forecastDateTimeMap= mutableMapOf<String,Forecast>()

                val forecastList=response.body()?.response?.body?.items?.forecastEntities.orEmpty()//널러블 해제
                for(forecast in forecastList){
                    Log.e("Forecast", forecast.toString())

                    if(forecastDateTimeMap["${forecast.forecastDate}/${forecast.forecastTime}"]==null){
                        forecastDateTimeMap["${forecast.forecastDate}/${forecast.forecastTime}"]=
                            Forecast(forecastDate=forecast.forecastDate, forecastTime =forecast.forecastTime )

                    }
                    forecastDateTimeMap["${forecast.forecastDate}/${forecast.forecastTime}"]?.apply {
                        when(forecast.category){
                            Category.POP-> precipitation=forecast.forecastValue.toInt()
                            Category.PTY-> precipitationType=transformRainType(forecast)
                            Category.SKY-> sky=skyType(forecast)
                            Category.TMP-> temperature=forecast.forecastValue.toDouble()
                            else ->{}

                        }
                    }


                }
                Log.e("Forecast", forecastDateTimeMap.toString())

            }

            override fun onFailure(call: Call<WeatherEntity>, t: Throwable) {
                t.printStackTrace()

            }

        })

    }

    private fun transformRainType(forecast: ForecastEntity):String{
       return when(forecast.forecastValue.toInt()){
            0->"없음"
            1->"비"
            2->"비/눈"
            3->"눈"
            4->"소나기"
            else->""
        }

    }
    private fun skyType(forecast: ForecastEntity):String{
        return when(forecast.forecastValue.toInt()){

            1->"맑음"
            3->"구름 많음"
            4->"흐림"
            else->""
        }

    }

}