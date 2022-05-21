package com.example.enginemeter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.enginemeter.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        moveToTest()
        moveToHistory()
        moveToTips()
        getCurrentDate()
        showBottomSheet()

    }

    private fun moveToTips() {
        binding.cardInfo.setOnClickListener {
            startActivity(Intent(this, TipsActivity::class.java))
            finish()
        }
    }

    private fun getCurrentDate(){
        val currentTime: Date = Calendar.getInstance().time
        val formatDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime)
        binding.tvDate.text = formatDate
    }

    private fun moveToTest(){
        binding.cardTes.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
            finish()
        }
    }

    private fun moveToHistory(){
        binding.cardRiwayat.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }
    }

    private fun showBottomSheet(){
        binding.tvAboutUs.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            bottomSheetDialog = BottomSheetDialog(this)
            bottomSheetDialog.setContentView(dialogView)
            bottomSheetDialog.show()
        }
    }

}