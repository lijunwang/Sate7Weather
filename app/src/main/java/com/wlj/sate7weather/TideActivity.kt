package com.wlj.sate7weather

import android.animation.ValueAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.model.LatLng
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.wlj.sate7weather.bean.TideBean
import com.wlj.sate7weather.utils.*
import com.wlj.sate7weather.viewmodel.TideViewMode
import kotlinx.android.synthetic.main.activity_tide.*
import java.text.SimpleDateFormat
import java.util.*

class TideActivity : AppCompatActivity() {
    private lateinit var tideViewMode:TideViewMode
    private val lineChartHeight by lazy {
        lineChart.height
    }
    private var menuItem:MenuItem? = null
    private var sheetOpened = true
    private fun testDateString(){
//        val str = "2020-11-08 13:47"
        val str = "2021-3-17 15:25"
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val date = simpleDateFormat.parse(str)
        val calendar = Calendar.getInstance()
        calendar.time = date
        val weekDays = listOf<String>("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六")
        val week = weekDays[calendar.get(Calendar.DAY_OF_WEEK + 1)]
        log("date $week")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tide)
        initToolBar()
        initMap()
        initLineChart()
        testDateString()
        tideViewMode = ViewModelProviders.of(this).get(TideViewMode::class.java)
        tideViewMode.getTideList(this)
        tideViewMode.tideList.observe(this){ tideBeanList ->
            if(tideBeanList == null){
                return@observe
            }
            val latLngList = tideBeanList.map {
                //drawPoint
                drawBitmap2Map(
                    tideMapView.map,
                    LatLng(it.lat, it.lon),
                    R.drawable.tide_position,
                    it
                )
                LatLng(it.lat, it.lon)
            }
            log("after map:${latLngList?.size}")
            val center = getCenter(latLngList)
            tideMapView.map.animateMapStatus(MapStatusUpdateFactory.newLatLng(center))
            tideMapView.map.animateMapStatus(
                MapStatusUpdateFactory.zoomTo(
                    2f + getSuitableZoomLevel(
                        latLngList
                    ).toFloat()
                )
            )
        }
    }

    private fun initToolBar(){
        setSupportActionBar(findViewById(R.id.toolBar))
        title = "潮汐站点"
        supportActionBar?.setHomeButtonEnabled(true)
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tide_trade, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                finish()
            }
            R.id.tide_close -> {
                openSheet(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun openSheet(item: MenuItem){
        var heightCurrent = lineChart.height
        menuItem = item
        log("openSheet ... $heightCurrent,$lineChartHeight")
        val valueAnimator = if(heightCurrent < lineChartHeight){
            item.setIcon(R.drawable.nav_list_close)
            sheetOpened = true
            ValueAnimator.ofInt(0, lineChartHeight)
        }else{
            item.setIcon(R.drawable.nav_list_open)
            sheetOpened = false
            ValueAnimator.ofInt(lineChartHeight, 0)
        }
        valueAnimator.addUpdateListener{
            lineChart.layoutParams.height = it.animatedValue as Int
            lineChart.requestLayout()
        }
        valueAnimator.duration = 300
        valueAnimator.start()

    }

    private fun initMap(){
        tideMapView.map.isMyLocationEnabled = true
        tideMapView.showZoomControls(false)
        val myLocationConfiguration =
            MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null)
        tideMapView.map.setMyLocationConfiguration(myLocationConfiguration)

        tideMapView.map.setOnMarkerClickListener { marker ->
            log("onMarkerClicked ... ")
            val dieBean = marker.extraInfo.getParcelable<TideBean>("extraData")
            dieBean?.let {
                log("tide detail info ...$it")
                tideViewMode.getDetailInfo(this, it.lon, it.lat)
                setData(lineChart, 7, 100f)
//                tideViewMode.getDetailInfoRaw(this,it.lon,it.lat)
//                Toast.makeText(this, "开发中...", Toast.LENGTH_SHORT).show()
                if(menuItem != null && !sheetOpened){
                    openSheet(menuItem!!)
                }
            }
            true
        }
    }

    private fun initLineChart(){
        val chart = lineChart
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.setDrawGridBackground(false)

        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(false)
        chart.setScaleEnabled(false)

        var xAxis: XAxis = chart.xAxis
        val week = arrayOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
        xAxis.labelCount = 6
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.mLabelHeight = 500
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return week[value.toInt() % 7]
            }
        }
        var yAxis: YAxis = chart.axisLeft
        chart.axisRight.isEnabled = false
        yAxis.axisMaximum = 200f
        yAxis.axisMinimum = -50f
        yAxis.valueFormatter = object :ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} cm"
            }
        }
        setData(chart, 7, 180f)
        chart.animateX(200)
        chart.legend.isEnabled = false;
//        val l: Legend = chart.legend
//        l.form = Legend.LegendForm.SQUARE
    }
    private fun setData(chart: LineChart, count: Int, range: Float) {
        log("setData ... $count,$range")
        val values = ArrayList<Entry>()
        for (i in 0 until count) {
            val value:Float = (Math.random() * range).toFloat() - 30
            values.add(Entry(i.toFloat(), value, null))
        }
        log("setData BB ... ")
        val set1 = LineDataSet(values, null)
        set1.setDrawIcons(false)
        set1.color = resources.getColor(R.color.charLine)
        set1.setCircleColor(resources.getColor(R.color.charCircle))
        // line thickness and point size
        set1.lineWidth = 5f
        set1.circleRadius = 7f
        // draw points as solid circles
        set1.circleHoleRadius = 2f
        set1.setDrawCircleHole(true)
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER
        // customize legend entry
        set1.formLineWidth = 1f
        set1.formSize = 15f
        // text size of values
        set1.valueTextSize = 9f
        // draw selection line as dashed
        set1.enableDashedHighlightLine(10f, 5f, 0f)
        // set the filled area
        set1.setDrawFilled(true)
        set1.fillFormatter =
            IFillFormatter { _, _ -> chart.axisLeft.axisMinimum }
        set1.setDrawFilled(false)
        val dataSets = ArrayList<ILineDataSet>()
        dataSets.add(set1) // add the data sets
        // create a data object with the data sets
        val data = LineData(dataSets)
        // set data
        chart.data = data
        chart.invalidate()
    }
}