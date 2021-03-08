package com.azhar.mylyrics.activities

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.azhar.mylyrics.R
import com.azhar.mylyrics.adapter.MainAdapter
import com.azhar.mylyrics.model.ModelMain
import com.azhar.mylyrics.networking.ApiEndpoint
import com.azhar.mylyrics.utils.OnItemClickCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    var modelMainList = ArrayList<ModelMain>()
    var mainAdapter: MainAdapter? = null
    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("Mohon Tunggu")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Sedang menampilkan lagu...")

        searchSong.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                setSearchSong(query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText == "") {
                    getListSongTop()
                }
                return false
            }
        })

        val searchPlateId = searchSong.getContext().resources
                .getIdentifier("android:id/search_plate", null, null)
        val searchPlate = searchSong.findViewById<View>(searchPlateId)
        searchPlate.setBackgroundColor(Color.TRANSPARENT)

        rvListMusic.setLayoutManager(LinearLayoutManager(this))
        rvListMusic.setHasFixedSize(true)

        getListSongTop()
    }

    private fun getListSongTop(){
            progressDialog?.show()
            AndroidNetworking.get(ApiEndpoint.BASEURl)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog?.dismiss()
                                val jsonArray = response.getJSONArray("data")
                                for (i in 0..10) {
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    val dataApi = ModelMain()
                                    dataApi.strId = jsonObject.getString("songId")
                                    dataApi.strArtis = jsonObject.getString("artist")
                                    dataApi.strTitle = jsonObject.getString("songTitle")
                                    modelMainList.add(dataApi)
                                }
                                showRecyclerSong()
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@MainActivity, "Gagal menampilkan data!",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog?.dismiss()
                            Toast.makeText(this@MainActivity, "Tidak ada jaringan internet!",
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    private fun setSearchSong(query: String) {
        progressDialog?.show()
        AndroidNetworking.get(ApiEndpoint.SEARCHURl)
                .addPathParameter("query", query)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        try {
                            progressDialog?.dismiss()
                            val jsonArray = response.getJSONArray("data")
                            for (i in 0 until jsonArray.length()) {
                                val jsonObject = jsonArray.getJSONObject(i)
                                val dataApi = ModelMain()
                                dataApi.strId = jsonObject.getString("songId")
                                dataApi.strArtis = jsonObject.getString("artist").replace("· ", "")
                                dataApi.strTitle = jsonObject.getString("songTitle")
                                modelMainList.add(dataApi)
                            }
                            showRecyclerSong()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity, "Gagal menampilkan data!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onError(anError: ANError) {
                        progressDialog?.dismiss()
                        Toast.makeText(this@MainActivity, "Tidak ada jaringan internet!", Toast.LENGTH_SHORT).show()
                    }
                })
    }

    private fun showRecyclerSong() {
        mainAdapter = MainAdapter(this, modelMainList)
        rvListMusic.adapter = mainAdapter
        mainAdapter?.setOnItemClickCallback(object : OnItemClickCallback {
            override fun onItemClicked(modelMain: ModelMain) {
                val intent = Intent(this@MainActivity, DetailLyricsActivity::class.java)
                intent.putExtra(DetailLyricsActivity.LIST_LYRICS, modelMain)
                startActivity(intent)
            }
        })
    }

}