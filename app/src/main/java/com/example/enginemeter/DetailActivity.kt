package com.example.enginemeter

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.enginemeter.databinding.ActivityDetailBinding
import com.example.enginemeter.room.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private var getId = 0
    val db by lazy { Database(this) }

    var speed = 0.0
    var time = 0.0

    // variabel fuzzy
    var rSpeedDouble :Array<Double> = arrayOf()
    var rSpeedString :Array<String> = arrayOf()

    var rTimeDouble :Array<Double> = arrayOf()
    var rTimeString :Array<String> = arrayOf()

    var inferenceDouble: Array<Double> = arrayOf()
    var inferenceString: Array<String> = arrayOf()

    var key: Int = 0
    var success: Boolean = false

    var disjunctionDouble: Array<Double> = arrayOf()
    var disjunctionString: Array<String> = arrayOf()

    // variabel defuzzyfication
    val rangeRendah = Array<Int>(5) { it * 7 }
    val rangeSedang = Array<Int>(5) { it * 8 + 36 }
    val rangeTinggi = Array<Int>(5) { it * 20 + 77 }
    var final_result = 0.0
    var string_result = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setData()
        backToHistory()

    }

    private fun backToHistory(){
        binding.toolbar.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setData(){
        try {
            getId = intent.getIntExtra("id", 0)
            CoroutineScope(Dispatchers.IO).launch {
                val getData = db.entityDao().getDataById(getId)[0]
                speed = getData.speed
                time = getData.time
                binding.tvInputSpeed.text = speed.toString()
                binding.tvInputTime.text = time.toString()
                binding.tvSuhu.text = getData.result.toString()

                fuzzyfication()
                inference()
                defuzzyfication()

                binding.tvResultSpeed.text = "${rSpeedDouble.toList()} => ${rSpeedString.toList()}"
                binding.tvResultTime.text = "${rTimeDouble.toList()} => ${rTimeString.toList()}"
                binding.tvResultConj.text = "${inferenceDouble.toList()} => ${inferenceString.toList()}"
                binding.tvResultDisjunc.text = "${disjunctionDouble.toList()} => ${disjunctionString.toList()}"
                binding.tvResultDefuzz.text = "$final_result => $string_result"
                binding.tvKategori.text = string_result

            }
        }catch (e: Exception){
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun fuzzyfication(){
        when{
            speed < 0.0 -> {
                rSpeedDouble += 0.0
            }
            speed in 0.0..35.0 -> {
                rSpeedString += "Rendah"
                rSpeedDouble += 1.0
            }
            speed in 36.0..44.0 ->{
                val a = 36.0
                val b = 44.0
                rSpeedString += "Rendah"
                rSpeedString += "Normal"
                rSpeedDouble += rumusPertama(speed,a,b)
                rSpeedDouble += rumusKedua(speed,a,b)

            }
            speed in 45.0..75.0 -> {
                rSpeedString += "Normal"
                rSpeedDouble += 1.0;
            }
            speed in 76.0..89.0 -> {
                val a = 76.0
                val b = 89.0
                rSpeedString += "Normal"
                rSpeedString += "Tinggi"
                rSpeedDouble += rumusPertama(speed,a,b)
                rSpeedDouble += rumusKedua(speed,a,b)
            };
            speed in 90.0..125.0 -> {
                rSpeedString +="Tinggi"
                rSpeedDouble += 1.0;
            }
            speed > 126.0 -> {
                rSpeedDouble += 0.0
            }
        }

        when {
            time < 0.0 -> rTimeDouble += 0.0
            time in 0.0..5.0 -> {
                rTimeString += "Singkat"
                rTimeDouble += 1.0
            }
            time in 6.0..10.0 -> {
                val a = 6.0
                val b = 10.0
                rTimeString += "Singkat"
                rTimeString += "Sedang"
                rTimeDouble += rumusPertama(time, a, b)
                rTimeDouble += rumusKedua(time, a, b)
            }
            time in 11.0..30.0 -> {
                rTimeString += "Sedang"
                rTimeDouble += 1.0
            }
            time in 31.0..40.0 -> {
                val a = 31.0
                val b = 40.0
                rTimeString += "Sedang"
                rTimeString += "Lama"
                rTimeDouble += rumusPertama(time, a, b)
                rTimeDouble += rumusKedua(time, a, b)
            }
            time in 41.0..120.0 -> {
                rTimeString += "Lama"
                rTimeDouble += 1.0
            }
            time > 121.0 -> {
                rTimeDouble += 0.0
            }
        }
    }

    private fun inference(){
        if(speed <= 0.0 || time <= 0.0){
            println("Data Jangan <=0 ")
        }else{
            if(speed > 126.0 || time > 121.0){
                Toast.makeText(applicationContext, "Data out of range", Toast.LENGTH_SHORT).show()
            }else{
                if(rSpeedString.isNotEmpty() && rTimeString.isNotEmpty()){
                    for(i in rSpeedDouble){
                        for(j in rTimeDouble){
                            if(i <= j ){
                                inferenceDouble += i
                            }else{
                                inferenceDouble += j
                            }
                        }
                    }
                    success = true
                }else{
                    Toast.makeText(applicationContext, "Some data is empty", Toast.LENGTH_SHORT).show()
                }
            }
        }

        for (i in rSpeedString) {
            for (j in rTimeString) {
                if (i == "Tinggi" && j == "Singkat") {
                    inferenceString += "Sedang"
                } else if (i == "Tinggi" && j == "Sedang") {
                    inferenceString += "Tinggi"
                } else if (i == "Tinggi" && j == "Lama") {
                    inferenceString += "Tinggi"
                } else if (i == "Normal" && j == "Singkat") {
                    inferenceString += "Sedang"
                } else if (i == "Normal" && j == "Sedang") {
                    inferenceString += "Sedang"
                } else if (i == "Normal" && j == "Lama") {
                    inferenceString += "Tinggi"
                } else if (i == "Rendah" && j == "Singkat") {
                    inferenceString += "Rendah"
                } else if (i == "Rendah" && j == "Sedang") {
                    inferenceString += "Sedang"
                } else if (i == "Rendah" && j == "Lama") {
                    inferenceString += "Tinggi"
                } else {
                    inferenceString += "Tidak Ada"
                }
            }
        }

        var cek = 0
        if(inferenceString.size > 2){
            for(i in inferenceString.indices){
                for(j in 1..inferenceString.size-1){
                    // Pengecekan apakah ada nama yang sama pada suatu array
                    if(inferenceString[0] == inferenceString[j]){
                        key = 1
                    }
                }
            }
        }else if(inferenceString.size == 2){
            if(inferenceString[0] == inferenceString[1]){
                cek = 1
            }
        }else{
            println("Lempar Defuzzi karena key = 0")
        }

        if(key == 1){
            disjunction(inferenceString)
        }else if(cek == 1){
            disjunctionString += inferenceString[0]
            if(inferenceDouble[0] > inferenceDouble[1]){
                disjunctionDouble += inferenceDouble[0]
            }else{
                disjunctionDouble += inferenceDouble[1]
            }
        }
        else{
            for(i in inferenceDouble){
                disjunctionDouble += i
            }
            for(i in inferenceString){
                disjunctionString += i
            }
        }

    }

    private fun disjunction(arr : Array<String>){
        var tempo : Array<Int> = arrayOf()
        var tempo2 : Array<Int> = arrayOf()
        var tempo3 : Array<Double> = arrayOf()
        var tempo4 : Array<Int> = arrayOf()
        var tempo5 : Array<Int> = arrayOf()

        // Mengecek nama yang sama pada array, jika ada dikelompokkan dan masukkan index tsb ke dalam array baru
        for(i in inferenceString.indices){
            if(inferenceString[0] == inferenceString[i]){
                tempo += i
            }else{
                tempo2 += i
            }
        }

        for(j in tempo2){
            val a = tempo2[0]
            if(inferenceString[a] == inferenceString[j]){
                tempo4 += j
            }else{
                tempo5 += j
            }
        }

        var result : Array<Double> = arrayOf()
        var result2 : Array<Double> = arrayOf()
        // Pengecekan Index, ambil index dgn nilai terbesar masukkan ke dalam variabel tempo3
        if(tempo5.isNotEmpty()){

            if(tempo.size == 2){
                // Ambil Nilai string dari index terbesar
                if(tempo[0] > tempo[1]){
                    val a = tempo[0]
                    disjunctionString += inferenceString[a]
                }else{
                    val a = tempo[1]
                    disjunctionString += inferenceString[a]
                }


                // Ambil nilai integer dari index terbesar
                for(i in tempo4){
                    result += inferenceDouble[i]
                }
                tempo3 += result.toList().sortedDescending().first()

                for(i in tempo4){
                    tempo3 += inferenceDouble[i]
                    disjunctionString += inferenceString[i]
                }
                for(i in tempo5){
                    tempo3 += inferenceDouble[i]
                    disjunctionString += inferenceString[i]
                }


            }else if(tempo4.size == 2){
                if(tempo4[0] > tempo4[1]){
                    val a = tempo4[0]
                    disjunctionString += inferenceString[a]
                }else{
                    val a = tempo4[1]
                    disjunctionString += inferenceString[a]
                }
                for(i in tempo4){
                    result += inferenceDouble[i]
                }
                tempo3 += result.toList().sortedDescending().first()
                for(i in tempo){
                    tempo3 += inferenceDouble[i]
                    disjunctionString += inferenceString[i]
                }
                for(i in tempo5){
                    tempo3 += inferenceDouble[i]
                    disjunctionString += inferenceString[i]
                }

            }else if(tempo5.size == 2){
                if(tempo5[0] > tempo5[1]){
                    val a = tempo5[0]
                    disjunctionString += inferenceString[a]
                }else{
                    val a = tempo5[1]
                    disjunctionString += inferenceString[a]
                }
                for(i in tempo5){
                    result += inferenceDouble[i]
                }
                tempo3 += result.toList().sortedDescending().first()
                for(i in tempo){
                    tempo3 += inferenceDouble[i]
                    disjunctionString += inferenceString[i]
                }
                for(i in tempo4){
                    tempo3 += inferenceDouble[i]
                    disjunctionString += inferenceString[i]
                }

            }else{
                println("error")
            }


        }else if(tempo.size == 2 && tempo2.size == 2){
            for(i in tempo){
                result += inferenceDouble[i]
            }
            tempo3 += result.toList().sortedDescending().first()
            result = arrayOf()

            if(tempo[0] > tempo[1]){
                val a = tempo[0]
                disjunctionString += inferenceString[a]
            }else{
                val a = tempo[0]
                disjunctionString += inferenceString[a]
            }

            if(tempo2[0] > tempo2[1]){
                val a = tempo2[0]
                disjunctionString += inferenceString[a]
            }else{
                val a = tempo2[0]
                disjunctionString += inferenceString[a]
            }

            for(i in tempo2){
                result += inferenceDouble[i]
            }

            result2 += result.toList().sortedDescending().first()

            for(i in result2){
                tempo3 += i
            }

        }else if(tempo.size == 3 && tempo2.size == 1){
            for(i in tempo){
                result += inferenceDouble[i]
            }
            tempo3 += result.toList().sortedDescending().first()

            // Ambil nilai string dari tempo3
            for(i in tempo3){
                disjunctionString += inferenceString[i.toInt()]
            }

            for(i in tempo2){
                disjunctionString += inferenceString[i]
                tempo3 += inferenceDouble[i]

            }

        }else if(tempo.size == 1 && tempo2.size == 3){
            for(i in tempo2){
                result += inferenceDouble[i]

            }
            tempo3 += result.toList().sortedDescending().first()

            // Ambil nilai string dari tempo3
            for(i in tempo3){
                disjunctionString += inferenceString[i.toInt()]
            }

            for(i in tempo){
                disjunctionString += inferenceString[i]
                tempo3 += inferenceDouble[i]

            }
        }else{
            println("Ups Ada data yang tidak valid")
        }

        disjunctionDouble += tempo3
    }

    private fun defuzzyfication(){
        if(disjunctionString.isNotEmpty()){
            if(disjunctionString.size == 3){
                if(disjunctionString[0] == "Rendah" && disjunctionString[1] == "Sedang" && disjunctionString[2] == "Tinggi"){
                    final_result = ( (rangeRendah.sum() * disjunctionDouble[0] ) + (rangeSedang.sum() * disjunctionDouble[1])+ (rangeTinggi.sum() * disjunctionDouble[2]) ) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5) + ( disjunctionDouble[2] * 5 ) + ( disjunctionDouble[3] * 5 ))
                }else if(disjunctionString[0] == "Rendah" && disjunctionString[1] == "Tinggi" && disjunctionString[2] == "Sedang"){
                    final_result = ( (rangeRendah.sum() * disjunctionDouble[0] ) + (rangeTinggi.sum() * disjunctionDouble[1])+ (rangeSedang.sum() * disjunctionDouble[2]) ) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5) + ( disjunctionDouble[2] * 5 ) + ( disjunctionDouble[3] * 5 ))
                }else if(disjunctionString[0] == "Sedang" && disjunctionString[1] == "Rendah" && disjunctionString[2] == "Tinggi"){
                    final_result = ( (rangeTinggi.sum() * disjunctionDouble[0] ) + (rangeSedang.sum() * disjunctionDouble[1])+ (rangeRendah.sum() * disjunctionDouble[2]) ) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5) + ( disjunctionDouble[2] * 5 ) + ( disjunctionDouble[3] * 5 ))
                }else if(disjunctionString[0] == "Sedang" && disjunctionString[1] == "Tinggi" && disjunctionString[2] == "Rendah"){
                    final_result = ( (rangeSedang.sum() * disjunctionDouble[0] ) + (rangeRendah.sum() * disjunctionDouble[1])+ (rangeTinggi.sum() * disjunctionDouble[2]) ) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5) + ( disjunctionDouble[2] * 5 ) + ( disjunctionDouble[3] * 5 ))
                }else if(disjunctionString[0] == "Tinggi" && disjunctionString[1] == "Rendah" && disjunctionString[2] == "Sedang"){
                    final_result = ( (rangeSedang.sum() * disjunctionDouble[0] ) + (rangeRendah.sum() * disjunctionDouble[1])+ (rangeTinggi.sum() * disjunctionDouble[2]) ) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5) + ( disjunctionDouble[2] * 5 ) + ( disjunctionDouble[3] * 5 ))
                }else if(disjunctionString[0] == "Tinggi" && disjunctionString[1] == "Sedang" && disjunctionString[2] == "Rendah"){
                    final_result = ( (rangeTinggi.sum() * disjunctionDouble[0] ) + (rangeRendah.sum() * disjunctionDouble[1])+ (rangeSedang.sum() * disjunctionDouble[2]) ) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5) + ( disjunctionDouble[2] * 5 ) + ( disjunctionDouble[3] * 5 ))
                }else{
                    println("Terjadi Kesalahan")
                }

            }else if(disjunctionString.size == 2){
                if(disjunctionString[0] == "Rendah" && disjunctionString[1] == "Sedang"){
                    final_result =  ( (rangeRendah.sum() * disjunctionDouble[0] ) + (rangeSedang.sum() * disjunctionDouble[1])) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5))
                }else if(disjunctionString[0] == "Rendah" && disjunctionString[1] == "Tinggi"){
                    final_result =  ( (rangeRendah.sum() * disjunctionDouble[0] ) + (rangeTinggi.sum() * disjunctionDouble[1])) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5))
                }else if(disjunctionString[0] == "Sedang" && disjunctionString[1] == "Rendah"){
                    final_result =  ( (rangeSedang.sum() * disjunctionDouble[0] ) + (rangeRendah.sum() * disjunctionDouble[1])) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5))
                }else if(disjunctionString[0] == "Sedang" && disjunctionString[1] == "Tinggi"){
                    final_result =  ( (rangeSedang.sum() * disjunctionDouble[0] ) + (rangeTinggi.sum() * disjunctionDouble[1])) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5))
                }else if(disjunctionString[0] == "Tinggi" && disjunctionString[1] == "Rendah"){
                    final_result =  ( (rangeTinggi.sum() * disjunctionDouble[0] ) + (rangeRendah.sum() * disjunctionDouble[1])) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5))
                }else if(disjunctionString[0] == "Tinggi" && disjunctionString[1] == "Sedang"){
                    final_result =  ( (rangeTinggi.sum() * disjunctionDouble[0] ) + (rangeSedang.sum() * disjunctionDouble[1])) / (( disjunctionDouble[0] * 5 ) + (disjunctionDouble[1] *5))
                }else{
                    println("Terjadi Kesalahan")
                }

            }else{
                if(disjunctionString[0] == "Rendah"){
                    final_result =   (rangeRendah.sum() * disjunctionDouble[0] ) / ( disjunctionDouble[0] * 5 )
                }else if(disjunctionString[0] == "Sedang"){
                    final_result =   (rangeSedang.sum() * disjunctionDouble[0] ) / ( disjunctionDouble[0] * 5 )
                }else if(disjunctionString[0] == "Tinggi"){
                    final_result =   (rangeTinggi.sum() * disjunctionDouble[0] ) / ( disjunctionDouble[0] * 5 )
                }else{
                    println("Terjadi Kesalahan")
                }
            }
        }

        if(success){
            if(final_result.toInt() in 1..35){
                string_result = "Rendah"
            }else if(final_result.toInt() in 36..76){
                string_result = "Sedang"
            }else if(final_result.toInt() in 77..177){
                string_result = "Tinggi"
            }else{
                string_result = "Data diluar jangkauan"
            }
        }else{
            if(speed > 120.0 && time > 120.0){
                string_result = "Nilai Kecepatan dan Nilai Durasi Tidak Logis"
            }else {
                string_result = "Nilai Kecepatan dan Nilai Durasi Tidak Logis"
            }

        }
    }

    private fun rumusPertama(x: Double,c :Double, d : Double):Double{
        val hitung = (-(x - d ))/(d - c)
        return hitung
    }

    private fun rumusKedua(x: Double,a :Double, b : Double):Double{
        val hitung = (x-a)/(b-a)
        return hitung
    }

}