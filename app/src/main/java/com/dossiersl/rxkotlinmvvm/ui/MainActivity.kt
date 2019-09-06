package com.dossiersl.rxkotlinmvvm.ui

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dossiersl.rxkotlinmvvm.R
import com.dossiersl.rxkotlinmvvm.databinding.ActivityMainBinding
import com.dossiersl.rxkotlinmvvm.viewmodel.MainViewModel

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.btn.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        viewModel.getCurrentWeatherDetails(this)
    }
}
