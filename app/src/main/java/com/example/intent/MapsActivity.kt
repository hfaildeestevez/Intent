package com.example.intent

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.intent.databinding.ActivityMapsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kiskos.apprepaso2.databinding.ActivityMapsBinding
import org.json.JSONObject
import org.json.JSONTokener

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private val PERMISO_LOCALIZACION: Int=3
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var database: DatabaseReference //variable database que la inicializo mas tarde
    private val TAG = "RealTime"
    private var datos = arrayOf<users>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = Firebase.database("https://apprepaso-c63ee-default-rtdb.europe-west1.firebasedatabase.app/").reference

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Texto para mostrar
        //val miTexto: TextView = findViewById(R.id.miTexto)
        //ler los datos cuando cambien
        val datoListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val dato = snapshot.getValue()
                Log.d(TAG,"cambio: "+ dato.toString())
                //miTexto.text = dato.toString()
                val jsonObject = JSONTokener(dato.toString()).nextValue() as JSONObject
                Log.d(TAG,"JSON: "+ jsonObject.getString("AA01"))

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG,"loadPost:onCancelled",error.toException())
            }
        }
        database.child("/users").addValueEventListener(datoListener)
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
        mMap.uiSettings.isMyLocationButtonEnabled=true
        //Compruebo los permisos
        enableMyLocation()
        //Hago visible los botones para apliar y desampliar el mapa
        mMap.uiSettings.isZoomControlsEnabled=true
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun comprobarPermisos(): Boolean {
        //Cuando
        when{
            //Si tengo permisos que me diga que tengo permisos
            ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED->{
                Log.i("Permisos","permiso garantozado")
                mensajeUsuario("Tienes Permisos")
                return true
            }
            //Si no los tengo por que los denegue que me salte un mensaje donde me diga que de los permisos en ajustes
            shouldShowRequestPermissionRationale (
                Manifest.permission.ACCESS_FINE_LOCATION
            )->{
                mensajeUsuario("Da permisos en ajustes")
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
    //Funcion para mostrar mensajes al usuario
    fun mensajeUsuario(mensaje:String){
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
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
    /**
     * @param userId Nombre del usuario
     * @param lt Latitud
     * @param lg Longitud
     */
    fun writeNewData(userId:String,lt:Double,lg:Double){
        Log.d(TAG,"Escribiendo Datos")
        val user = Users(userId,lg,lt,userId)
        database.child("users/AA03").setValue(user)

    }

}