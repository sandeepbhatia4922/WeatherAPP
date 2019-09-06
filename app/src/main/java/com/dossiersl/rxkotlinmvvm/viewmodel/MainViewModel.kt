package com.dossiersl.rxkotlinmvvm.viewmodel

import android.app.Application
import android.app.Dialog
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.widget.Toast
import com.dossiersl.rxkotlinmvvm.model.CurrentWeatherResponse
import com.dossiersl.rxkotlinmvvm.retrofit.ApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException

class MainViewModel(application: Application) : AndroidViewModel(application) {


    val response: MutableLiveData<CurrentWeatherResponse> = MutableLiveData()

    fun getCurrentWeatherDetails(ctx: Context,city:String) {
        val dialog = Dialog(ctx)
        dialog.setTitle("loading")
        dialog.setCancelable(false)
        dialog.show()
        val apiService = ApiClient.create()
        var observable = apiService.currentWeather(ApiClient.KEY, city)

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    dialog.dismiss()
                    response.value = result

                },
                { error ->
                    dialog.dismiss()
                    if (error is HttpException) {
                        Toast.makeText(ctx, "some thing went wrong", Toast.LENGTH_SHORT).show()
                    }
                }
            )


    }
}