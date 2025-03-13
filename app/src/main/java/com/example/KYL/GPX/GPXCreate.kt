package com.example.KYL.GPX

import com.example.KYL.constans.MainTime
import com.example.KYL.constans.StringForGPX
import com.example.KYL.entities.CoordinateLatLongName

class GPXCreate(pointList: List<CoordinateLatLongName>) {

    private val GPXString = createGPXString(createGPXPoint(pointList))
    fun getGPXString() = GPXString

    private fun createGPXPoint(pointList: List<CoordinateLatLongName>): List<WPTpoint> {
        val poinWPTList: MutableList<WPTpoint> = emptyList<WPTpoint>().toMutableList()
        for (i in 0..pointList.size - 1) {
            val pointWPT = WPTpoint(
                lat = pointList[i].latitude.toFloat(),
                lon = pointList[i].longitude.toFloat(),
                ele = 0.00000f,
                time = MainTime.getTimeForGPX(),
                name = pointList[i].name,
                cmt = pointList[i].note,
                desc = pointList[i].note
            )
            poinWPTList.add(pointWPT)
        }
        return poinWPTList
    }

    private fun createGPXString(pointList: List<WPTpoint>): String {
        val gpxStrind: String

        val stringBuilder = StringBuilder(StringForGPX.METADATA)
        stringBuilder.append(StringForGPX.TIME_1)
        stringBuilder.append(MainTime.getTimeForGPX())
        stringBuilder.append(StringForGPX.TIME_2)
        stringBuilder.append(StringForGPX.BOUNDS)
        stringBuilder.append(pointList[0].lat)
        stringBuilder.append(StringForGPX.MINILON)
        stringBuilder.append(pointList[0].lon)
        stringBuilder.append(StringForGPX.MAXLAT)
        stringBuilder.append(pointList[pointList.size - 1].lat)
        stringBuilder.append(StringForGPX.MAXLON)
        stringBuilder.append(pointList[pointList.size - 1].lon)
        stringBuilder.append(StringForGPX.MAXLON_2)

        for (i in 0..pointList.size - 1) {
            stringBuilder.append(StringForGPX.WPT_LAT)
            stringBuilder.append(pointList[i].lat)
            stringBuilder.append(StringForGPX.WPT_LON)
            stringBuilder.append(pointList[i].lon)
            stringBuilder.append(StringForGPX.WPT_LON_2)
            stringBuilder.append(StringForGPX.ELE)
            stringBuilder.append(StringForGPX.TIME_1)
            stringBuilder.append(pointList[i].time)
            stringBuilder.append(StringForGPX.TIME_2)
            stringBuilder.append(StringForGPX.NAME)
            stringBuilder.append(pointList[i].name)
            stringBuilder.append(StringForGPX.NAME_2)
            stringBuilder.append(StringForGPX.CMT)
            stringBuilder.append(pointList[i].cmt)
            stringBuilder.append(StringForGPX.CMT_2)
            stringBuilder.append(StringForGPX.DESC)
            stringBuilder.append(pointList[i].desc)
            stringBuilder.append(StringForGPX.DESC_2)
        }

        stringBuilder.append(StringForGPX.GPX_2)
        gpxStrind = stringBuilder.toString()
        return gpxStrind
    }


}