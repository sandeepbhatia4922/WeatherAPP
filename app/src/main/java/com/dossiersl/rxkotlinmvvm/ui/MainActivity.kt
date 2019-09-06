package com.dossiersl.rxkotlinmvvm.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.*
import android.location.LocationListener
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import android.view.View
import com.dossiersl.rxkotlinmvvm.R
import com.dossiersl.rxkotlinmvvm.Utils
import com.dossiersl.rxkotlinmvvm.databinding.ActivityMainBinding
import com.dossiersl.rxkotlinmvvm.viewmodel.MainViewModel
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import java.lang.Exception
import android.arch.lifecycle.Observer
import com.bumptech.glide.Glide
import java.util.*

class MainActivity : AppCompatActivity(),View.OnClickListener, LocationListener {


    private lateinit var viewModel: MainViewModel
    private lateinit var binding : ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private lateinit var dialog: ProgressDialog
    private lateinit var cityName: String
    private var isGPSEnabled = false
    private var isNetworkEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.btn.setOnClickListener(this)
        binding.btnLoc.setOnClickListener(this)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        obserweatherUpDates()
    }

    private fun obserweatherUpDates() {
        viewModel.response.removeObservers(this)
        viewModel.response.observe(this,Observer { model->

            if(model!= null)
            {
                binding.condition.text = model.current.condition.text

                Glide.with(this).load("https:${model.current.condition.icon}").into(binding.img)


            }


        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onClick(v: View?) {

        when (v?.id) {

            R.id.btn -> viewModel.getCurrentWeatherDetails(this,binding.btnLoc.text.toString())

            R.id.btn_loc ->
            {
                if (Utils.checkPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                    setLocationOn()

                } else {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        100
                    )
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    fun setLocationOn() {

        val builder: LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(
            LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000)
        )
        builder.setAlwaysShow(true)
        val mLocationSettingsRequest: LocationSettingsRequest = builder.build()
        val settingClient: SettingsClient = LocationServices.getSettingsClient(this)

        settingClient.checkLocationSettings(mLocationSettingsRequest)
            .addOnSuccessListener {

                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)


                if (!isGPSEnabled && !isNetworkEnabled) {

                    setLocationOn()

                } else {

                    if (isNetworkEnabled) {

                        dialog = ProgressDialog(this)
                        dialog.setTitle("Getting current location")
                        dialog.show()
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)

                    }

                }


            }
            .addOnCanceledListener {
                Log.e("GPS", "checkLocationSettings -> onCanceled")


            }
            .addOnFailureListener { e: Exception ->
                val statusCode = (e as ApiException).statusCode
                when (statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            var rae = e as ResolvableApiException
                            this.startIntentSenderForResult(
                                rae.resolution.intentSender,
                                101,
                                null,
                                0,
                                0,
                                0,
                                null
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e("GPS", "Unable to execute request.")
                        }
                    }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        Log.e(
                            "GPS",
                            "Location settings are inadequate, and cannot be fixed here. Fix in Settings."
                        )
                    }

                }
            }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    if (!isGPSEnabled && !isNetworkEnabled) {

                        setLocationOn()

                    } else {
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
                    }

                }
            }

        }
    }
    override fun onLocationChanged(location: Location?) {

        val geocoder = Geocoder(this, Locale.getDefault())
        val address: List<Address>
        if (location != null) {
            dialog.dismiss()
            address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (address.size > 0) {
                cityName = address.get(0).getAddressLine(0)

                binding.btnLoc.text = cityName

            }

            Log.d("Address", "$cityName - ${location.latitude}, ${location.longitude}")

            locationManager.removeUpdates(this)
        }

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String?) {

    }

    override fun onProviderDisabled(provider: String?) {

    }
}
