package com.example.flightmobileapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flightmobileapp.databinding.ActivityMainBinding
import com.example.flightmobileapp.db.UrlDataBase
import com.example.flightmobileapp.db.UrlFile
import com.example.flightmobileapp.db.UrlRepository
import com.example.flightmobileapp.viewmodel.UrlViewModel
import com.example.flightmobileapp.viewmodel.UrlViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_screenshot.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var urlViewModel: UrlViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = UrlDataBase.getInstance(application).urlDAO
        val repository = UrlRepository(dao)
        val factory =
            UrlViewModelFactory(repository)
        urlViewModel = ViewModelProvider(this,factory).get(UrlViewModel::class.java)
        binding.myViewModel = urlViewModel
        binding.lifecycleOwner = this
        initRecycleView()
        urlViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        urlViewModel.isConnected.observe(this, Observer {
            if(it){
                //val myUrl = urlViewModel.typeURLtemp.toString()
                val myUrl = urlViewModel.typeURLtemp
                val intent = Intent(this,ScreenshotActivity::class.java)
                intent.putExtra("myUrl", myUrl)
                startActivity(intent)
            }
        })
    }
    private fun initRecycleView(){
        binding.urlRecycleView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({selectItem:UrlFile->listItemClicked(selectItem)})
        binding.urlRecycleView.adapter = adapter
        displayUrlsList()
    }
    private fun displayUrlsList() {
        urlViewModel.urls.observe(this, Observer {
            Log.i("MYTAG", it.toString())
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }
    private fun listItemClicked(urlFile: UrlFile){
        //Toast.makeText(this, "selected URL is ${urlFile.name}", Toast.LENGTH_LONG).show()
        urlViewModel.initUrlTextClicked(urlFile)
    }
}