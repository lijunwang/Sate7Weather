package com.wlj.sate7weather.utils

import android.os.Bundle
import android.os.Parcelable
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.model.LatLngBounds
import com.baidu.mapapi.utils.DistanceUtil
import com.wlj.sate7weather.R
import com.wlj.sate7weather.bean.TyphoonDetailInfo

fun drawLine(baiduMap: BaiduMap, detailInfoList: List<TyphoonDetailInfo>){
    //标识当前路径
    /*detailInfoList?.let {
        val middle = it.size / 2
        val item = it[middle]
        val point = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
        var middleMarkerOptions = MarkerOptions()
            .position(point)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.typhoon_focus))
            .clickable(true)
            .zIndex(2)
        baiduMap.addOverlay(middleMarkerOptions)
    }*/
    val pointLists = ArrayList<LatLng>()
    //绘制每个点的信息
    detailInfoList.iterator().forEach { item ->
        val point = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
        pointLists.add(point)
        val data = Bundle()
        data.putParcelable("TyphoonDetailInfo", item)
        var markerOptions = MarkerOptions()
            .position(point)
            .icon(getIconForTyphoon(item))
            .clickable(true)
            .extraInfo(data)
            .zIndex(9)
        baiduMap.addOverlay(markerOptions)
    }
    //绘制台风路线
    val polylineOptions = PolylineOptions()
        .width(5)
        .color(-0x55e59a3f)
        .points(pointLists)
    baiduMap.addOverlay(polylineOptions)
    //缩放
    //添加动态缩放
    val latLngBounds: LatLngBounds = getBounds(pointLists)
    val statusUpdate = MapStatusUpdateFactory.newLatLngBounds(latLngBounds)
    baiduMap.animateMapStatus(statusUpdate)
    val center: LatLng = getCenter(latLngBounds)
    val move = MapStatusUpdateFactory.newLatLng(center)
    baiduMap.setMapStatus(move)
}
//TODO 服务器需要添加一个返回接口  中心风力
private fun getIconForTyphoon(detailInfo: TyphoonDetailInfo): BitmapDescriptor? {
    //暂时用speed代替
    val speed = detailInfo.speed.toDouble()
    return if(speed >= 13){
        BitmapDescriptorFactory.fromResource(R.drawable.icon_13)
    }else if(speed > 10 && speed<=12){
        BitmapDescriptorFactory.fromResource(R.drawable.icon_10_12)
    }else if(speed > 8 && speed<=10){
        BitmapDescriptorFactory.fromResource(R.drawable.icon_8_10)
    }else if(speed > 6 && speed<=8){
        BitmapDescriptorFactory.fromResource(R.drawable.icon_6_8)
    }else if(speed > 1 && speed<=6){
        BitmapDescriptorFactory.fromResource(R.drawable.icon_1_6)
    }else{
        BitmapDescriptorFactory.fromResource(R.drawable.icon_1_6)
    }
}
private fun getBounds(pointList: List<LatLng>): LatLngBounds {
    require(pointList.isNotEmpty()) { "getBounds pointList must not be null !!" }
    var north = pointList[0].latitude
    var south = pointList[0].latitude
    var east = pointList[0].longitude
    var west = pointList[0].longitude
    for (point in pointList) {
        if (point.latitude > north) {
            north = point.latitude
        }
        if (point.latitude < south) {
            south = point.latitude
        }
        if (point.longitude > east) {
            east = point.longitude
        }
        if (point.longitude < west) {
            west = point.longitude
        }
    }
    val northEast = LatLng(north, east)
    val southWest = LatLng(south, west)
    return LatLngBounds.Builder().include(northEast).include(southWest).build()
}

private fun getCenter(latLngBounds: LatLngBounds): LatLng {
    //找到最东北的点和最东南的点，自动进行缩放
    val northEast = latLngBounds.northeast
    val southWest = latLngBounds.southwest
    return LatLng(
        northEast.latitude / 2 + southWest.latitude / 2,
        northEast.longitude / 2 + southWest.longitude / 2
    )
}

fun getCenter(pointList: List<LatLng>):LatLng{
    return getBounds(pointList).center
}

fun getSuitableZoomLevel(latLng:List<LatLng>): Int {

//        String[] distance = new String[]{"10m", "20m", "50m", "100m", "200m", "500m", "1km", "2km", "5km", "10km", "20km", "25km", "50km", "100km", "200km", "500km", "1000km", "2000km"};
    val distance = intArrayOf(
        10,
        20,
        50,
        100,
        200,
        500,
        1000,
        2000,
        5000,
        10000,
        20000,
        25000,
        50000,
        100000,
        200000,
        500000,
        1000000,
        2000000
    )
    val level = intArrayOf(20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3)
    var temp: LatLng? = latLng[0]
    var maxDistance = 0.0
    var currentDistance = 0.0
    for (latLng in latLng) {
        currentDistance = DistanceUtil.getDistance(temp, latLng)
        if (currentDistance > maxDistance) {
            maxDistance = currentDistance
        }
        temp = latLng
    }
    log("getZoomLevel maxDistance == $maxDistance")
    var levelIndex = -1
    for (i in distance.indices.reversed()) {
        if (distance[i] > maxDistance) {
            continue
        } else {
            levelIndex = i
            break
        }
    }
    var levelFinal = -1
    if (levelIndex > -1) {
        levelFinal = level[levelIndex]
    }
    log("getZoomLevel levelIndex , levelFinal == $levelIndex , $levelFinal")
    return levelFinal
}

fun drawBitmap2Map(map: BaiduMap, position: LatLng, iconId: Int,extraData:Parcelable?){
    val data = Bundle()
    data.putParcelable("extraData",extraData)
    val iconOption: OverlayOptions = MarkerOptions()
        .position(position)
        .icon(BitmapDescriptorFactory.fromResource(iconId))
        .clickable(true)
        .extraInfo(data)
        .zIndex(9)
    map.addOverlay(iconOption)
}
