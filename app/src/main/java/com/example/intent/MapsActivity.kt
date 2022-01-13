package com.example.intent

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.intent.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        enableMyLocation()
    }

    /**
     * Función para comprobar permiso de localización
     */
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED //Para saber si los permisos estan activados o no


    /**
     * Función para comprobar si se ha iniciado el Mapa
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (!::mMap.isInitialized) return
        if (isPermissionsGranted()) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    /**
     * Función para solicitar permisos
     */
    companion object {
        //Para saber si es nuestro permiso el aceptado
        const val REQUEST_CODE_LOCATION = 0
    }

    private fun requestLocationPermission() {
        //Si entra en if es que se han rechazado los permisos
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                this,
                "Please go to settings and accept the permissions",
                Toast.LENGTH_SHORT

            ).show()
        } else {
            //Si entra en el else, significa que nunca se pidieron permisos
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
            }else {
                Toast.makeText(
                    this,
                    "To activate the location go to settings and accept the permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else ->{}
        }
    }

    /**
     * Función para que no rompa la app
     */
    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        //Si el mapa ha sido creado
        if(!::mMap.isInitialized) return
        //Si los permisos esta activos
        if(!isPermissionsGranted()){
            //En caso de no ser así, desactivamos localización en tiempo real
            mMap.isMyLocationEnabled = false
            Toast.makeText(
                this,
                "To activate the location go to settings and accept the permissions",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
