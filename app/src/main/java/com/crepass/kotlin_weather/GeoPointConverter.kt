package com.crepass.kotlin_weather

import kotlin.math.PI

class GeoPointConverter {

    private val NX=149
    private val NY=253

    private val RE = 6371.0087 //지도 반경
    private val GRID= 5.0//격자 간격
    private val SLAT1=30.0//표준 위도 1
    private val SLAT2=60.0//표준 위도 2
    private val OLON=126.0//기준점 경도
    private val OLAT=38.0//기준점 위도
    private val XO=210/GRID
    private val YO=675/GRID


    private val DEGRAD= PI/180.0
    private val RADDEG=180.0/PI

}