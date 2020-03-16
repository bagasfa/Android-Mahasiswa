package com.example.loginmahasiswa

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.json.JSONArray
import org.json.JSONObject

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        button.setOnClickListener{
            val sharedPreferences=getSharedPreferences("CEKLOGIN", Context.MODE_PRIVATE)
            val editor=sharedPreferences.edit()

            editor.putString("STATUS","0")
            editor.apply()

            startActivity(Intent(this@DashboardActivity,MainActivity::class.java))
            finish()
        }

        btn_save.setOnClickListener {
            val nama = editTextNama.text.toString()
            val nomor = editTextNomor.text.toString()
            val alamat = editTextAlamat.text.toString()
            postServer(nama, nomor, alamat)
            Log.i("result",nama+nomor+alamat)
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        //View Data

        val recyclerView = findViewById(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager= LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        val mahasiswa=ArrayList<Mahasiswa>()

        AndroidNetworking.get("http://$ip/LoginMhs/mahasiswa.php")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Log.i("_kotlinResponse", response.toString())

                    val jsonArray = response.getJSONArray("result")
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        Log.i("_kotlinTitle", jsonObject.optString("nama_mahasiswa"))
                        Log.i("_kotlinTitle", jsonObject.optString("nomer_mahasiswa"))
                        Log.i("_kotlinTitle", jsonObject.optString("alamat_mahasiswa"))

                        var isi1=jsonObject.optString("nama_mahasiswa").toString()
                        var isi2=jsonObject.optString("nomer_mahasiswa").toString()
                        var isi3=jsonObject.optString("alamat_mahasiswa").toString()

                        mahasiswa.add(Mahasiswa("$isi1", "$isi2", "$isi3"))
                    }

                    val adapter=MahasiswaAdapter(mahasiswa)
                    recyclerView.adapter=adapter
                }

                override fun onError(anError: ANError) {
                    Log.i("_err", anError.toString())
                }
            })
    }

    fun postServer(data1: String, data2: String, data3: String) {
        Log.i("result",data1+data2+data3)
        AndroidNetworking.post("http://$ip/LoginMhs/mahasiswa_proses.php")
            .addBodyParameter("nama_mahasiswa", data1)
            .addBodyParameter("nomer_mahasiswa", data2)
            .addBodyParameter("alamat_mahasiswa", data3)
            .setPriority(Priority.MEDIUM).build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                }
                override fun onError(anError: ANError?) {
                    Log.i("_err", anError.toString())
                }
            })
    }
}
