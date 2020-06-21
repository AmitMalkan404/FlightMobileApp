package com.example.flightmobileapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.flightmobileapp.databinding.ListItemBinding
import com.example.flightmobileapp.db.UrlFile
import com.example.flightmobileapp.generated.callback.OnClickListener
import java.net.URL

class MyRecyclerViewAdapter(private val clickListener:(UrlFile)->Unit)
    :RecyclerView.Adapter<MyViewHolder>()
{
    private val urlFiles = ArrayList<UrlFile>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding:ListItemBinding  =
            DataBindingUtil.inflate(layoutInflater,R.layout.list_item,parent,false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return urlFiles.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(urlFiles[position],clickListener)
    }
    fun setList(urlFile: List<UrlFile>){
        urlFiles.clear()
        urlFiles.addAll(urlFile)
    }
}
class MyViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(urlFile: UrlFile,clickListener:(UrlFile)->Unit){
        binding.urlTextView.text = urlFile.name
        binding.listItemLayout.setOnClickListener{
            clickListener(urlFile)
        }
    }
}