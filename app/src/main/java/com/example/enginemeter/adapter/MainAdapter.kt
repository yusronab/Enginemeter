package com.example.enginemeter.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.enginemeter.databinding.ListResultBinding
import com.example.enginemeter.model.MainModel

class MainAdapter(private val data: MutableList<MainModel>, private val listener: AdapterClick) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ListResultBinding) : RecyclerView.ViewHolder(binding.root)

    interface AdapterClick{
        fun onRead(data: MainModel)
        fun onDelete(data: MainModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            tvResult.text = data[position].result.toString()

            cvResult.setOnClickListener {
                listener.onRead(data[position])
            }

            iconDelete.setOnClickListener {
                listener.onDelete(data[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun setData(d: List<MainModel>){
        data.clear()
        data.addAll(d)
        notifyDataSetChanged()
    }

}