package com.example.mts_maps

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.mts_maps.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.*
import java.io.IOException
import java.net.URL
import kotlin.math.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private var isAddMarker = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (this as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name_mts)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        //converter(48.725205544602304, 44.62335179873284, 11)
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//        val img: Bitmap? = getBitmap()
//        if (img != null){
//            mMap.addMarker(MarkerOptions()
//                .position(LatLng(48.725205544602304, 44.62335179873284))
//                .icon(BitmapDescriptorFactory.fromBitmap(img))
//            )
//        }
    }

    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100
            )
            return
        } else {
            Log.d("locationManager", "locationManager requestLocationUpdates")
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                10f,
                locationListener
            )
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                1000,
                10f,
                locationListener
            )
        }

    }

    private fun converterToLatLng(x: Int, y: Int, z: Int) : List<Float>{
        val n = 2f.pow(z)
        val lon_deg = x / n * 360 - 180
        val lat_rad = atan(Math.sinh(Math.PI * (1-2 * y / n)))
        val lat_deg = (lat_rad * 180) / PI
        return listOf(lat_deg.toFloat(), lon_deg)
    }

    private fun converterToPx(lat: Double, long: Double, z: Int): List<Int> {
        val e = 0.0818191908426
        val rho = 2f.pow(z + 8) / 2
        val beta = lat * PI / 180
        val phi = (1 - e * sin(beta)) / (1 + e * sin(beta))
        val theta = tan(PI / 4 + beta / 2) * phi.pow(e / 2)

        val x_p = rho * (1 + long / 180) / 256
        val y_p = rho * (1 - ln(theta) / PI) / 256

        Log.d("converter", listOf(x_p.toInt(), y_p.toInt()).toString())
        return listOf(x_p.toInt(), y_p.toInt())
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            Toast.makeText(this@MapsActivity, location.toString(), Toast.LENGTH_LONG).show()
            setMyLocation(LatLng(location.latitude, location.longitude))
        }

        override fun onProviderDisabled(provider: String) {

        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }
    }

    private fun setMyLocation(latLng: LatLng) {
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 11f)
        mMap.moveCamera(cameraUpdate)
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        if (!isAddMarker) {
            addPng(latLng)
            mMap.addMarker(MarkerOptions().position(latLng))
            isAddMarker = true
        }
    }

    private fun addPng(latLng: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            val pxLatLng = converterToPx(latLng.latitude, latLng.longitude, 11)
            val latlngList: MutableList<List<Float>> = mutableListOf()
            var bitmap1: Bitmap? = null
            var bitmap2: Bitmap? = null
            var bitmap3: Bitmap? = null
            var bitmap4: Bitmap? = null
            var bitmap5: Bitmap? = null
            var bitmap6: Bitmap? = null
            var bitmap7: Bitmap? = null
            var bitmap8: Bitmap? = null
            var bitmap9: Bitmap? = null
            val bitmapList: MutableList<Bitmap?> = mutableListOf()
            withContext(Dispatchers.IO) {
                try {
                    var x = pxLatLng[0]
                    var y = pxLatLng[1]
                    val url1 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x--}/${y--}/")
                    bitmap1 = BitmapFactory.decodeStream(url1.openConnection().inputStream)
                    bitmapList.add(bitmap1)
                    latlngList.add(converterToLatLng(x--, y--, 11))

                    val url2 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x}/${y--}/")
                    bitmap2 = BitmapFactory.decodeStream(url2.openConnection().inputStream)
                    bitmapList.add(bitmap2)
                    latlngList.add(converterToLatLng(x, y--, 11))

                    val url3 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x++}/${y--}/")
                    bitmap3 = BitmapFactory.decodeStream(url3.openConnection().inputStream)
                    bitmapList.add(bitmap3)
                    latlngList.add(converterToLatLng(x++, y--, 11))

                    val url4 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x--}/${y}/")
                    bitmap4 = BitmapFactory.decodeStream(url4.openConnection().inputStream)
                    bitmapList.add(bitmap4)
                    latlngList.add(converterToLatLng(x--, y, 11))

                    val url5 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x}/${y}/")
                    bitmap5 = BitmapFactory.decodeStream(url5.openConnection().inputStream)
                    bitmapList.add(bitmap5)
                    //Log.d("converter-latlng", converterToLatLng(x,y, 11).toString())
                    latlngList.add(converterToLatLng(x, y, 11))

                    val url6 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x++}/${y}/")
                    bitmap6 = BitmapFactory.decodeStream(url6.openConnection().inputStream)
                    bitmapList.add(bitmap6)
                    latlngList.add(converterToLatLng(x++, y, 11))

                    val url7 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x--}/${y++}/")
                    bitmap7 = BitmapFactory.decodeStream(url7.openConnection().inputStream)
                    bitmapList.add(bitmap7)
                    latlngList.add(converterToLatLng(x--, y++, 11))

                    val url8 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x}/${y++}/")
                    bitmap8 = BitmapFactory.decodeStream(url8.openConnection().inputStream)
                    bitmapList.add(bitmap8)
                    latlngList.add(converterToLatLng(x, y++, 11))

                    val url9 =
                        URL("https://tiles.qsupport.mts.ru/lte_New/11/${x++}/${y++}/")
                    bitmap9 = BitmapFactory.decodeStream(url9.openConnection().inputStream)
                    bitmapList.add(bitmap9)
                    latlngList.add(converterToLatLng(x++, y++, 11))

                } catch (e: IOException) {
                    Log.d("check-bitmap", e.toString())
                }
            }

            if (bitmapList.size != 0) {
                Log.d("check-bitmap", "bitmapList size is ${bitmapList.size}")
                var i : Int = 0
                bitmapList.forEach {
                    var list = latlngList[i]
                    var lat = list[0]
                    var long = list[1]
                    mMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(lat.toDouble(), long.toDouble()))
                            .icon(BitmapDescriptorFactory.fromBitmap(it))
                    )
                    i++
                }
            } else {
                Log.d("check-bitmap", "bitmapList size is 0")
            }


        }
    }
}