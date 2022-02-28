package com.example.intent.ui.home

import androidx.fragment.app.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.intent.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MapsFragment : Fragment() {
    private val PERMISO_LOCALIZACION: Int=3
    private lateinit var mMap: GoogleMap
    private lateinit var database: DatabaseReference
    private val TAG = "RealTime"
    private val callback = OnMapReadyCallback {
        //le digo que el mapa de clase es igual al mapa que instancio
        mMap=it
        database = Firebase.database("https://apprepaso-c63ee-default-rtdb.europe-west1.firebasedatabase.app/").reference
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap.uiSettings.isMyLocationButtonEnabled=true
        //Compruebo los permisos
        enableMyLocation()
        //Hago visible los botones para apliar y desampliar el mapa
        mMap.uiSettings.isZoomControlsEnabled=true

        val castelao = LatLng(42.23664789365711, -8.714183259832732)
        mMap.addMarker(MarkerOptions().position(castelao).title("Sentro refashero"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(castelao))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
    private fun comprobarPermisos(): Boolean {
        //Cuando
        when{
            //Si tengo permisos que me diga que tengo permisos
            context?.let { ContextCompat.checkSelfPermission(it.applicationContext,android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED->{
                Log.i("Permisos","permiso garantozado")
                return true
            }
            //Si no los tengo por que los denegue que me salte un mensaje donde me diga que de los permisos en ajustes
            shouldShowRequestPermissionRationale (
                Manifest.permission.ACCESS_FINE_LOCATION
            )->{
                return false
            }
            //La Primera vez que me pide los permisos  tengo la opcion de aceptar o no
            else->{
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),PERMISO_LOCALIZACION)
                return false
            }
        }
    }
    //Con esta funcion Compruebo que le di correctamente lso permisos
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            PERMISO_LOCALIZACION->{ //Conpruebo si mi permiso no esta vacio y fue dado
                if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    mMap.isMyLocationEnabled = true //que me muestre en el mapa
                }
            }
            //Para los demas permisos
            else->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }
    //con este metodo compruebo que se inicialice el mapa y hago la comprobacion de permisos
    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if(!::mMap.isInitialized) return
        if(comprobarPermisos()){
            mMap.isMyLocationEnabled = true
        } else{
            comprobarPermisos()
        }
    }
}