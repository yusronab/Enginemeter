package com.example.enginemeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.enginemeter.adapter.MainAdapter
import com.example.enginemeter.databinding.ActivityHistoryBinding
import com.example.enginemeter.model.MainModel
import com.example.enginemeter.room.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    lateinit var mainAdapter: MainAdapter
    val db by lazy { Database(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backToMenu()
        readData()
        setRecycler()

    }

    private fun backToMenu(){
        binding.toolbar.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun readData(){
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val data = db.entityDao().getAllData()
                withContext(Dispatchers.Main){
                    mainAdapter.setData(data)
                }
            }
        }catch (e: Exception){
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setRecycler(){
        mainAdapter = MainAdapter(mutableListOf(), object : MainAdapter.AdapterClick {
            override fun onRead(data: MainModel) {
                startActivity(Intent(this@HistoryActivity, DetailActivity::class.java).also {
                    it.putExtra("id", data.id)
                    finish()
                })
            }

            override fun onDelete(data: MainModel) {
                deleteDialog(data)
            }


        })
        binding.rvResult.apply {
            adapter = mainAdapter
            layoutManager = LinearLayoutManager(this@HistoryActivity)
        }
    }

    private fun deleteDialog(note: MainModel){
        val alert = AlertDialog.Builder(this)
        alert.apply {
            setTitle("Delete Alert")
            setMessage("Apakah kamu yakin ingin menghapus data ini?")
            setPositiveButton("Yakin"){dialog, which ->
                try {
                    CoroutineScope(Dispatchers.IO).launch {
                        db.entityDao().deleteData(note)
//                            Toast.makeText(applicationContext, "Berhasil Menghapus Catatan", Toast.LENGTH_SHORT).show()
                        readData()
                    }
                }catch (e: Exception){
                    Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            setNegativeButton("Batalkan"){dialog, which ->
                dialog.dismiss()
            }
            show()
        }
    }

}