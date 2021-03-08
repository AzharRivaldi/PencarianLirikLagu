package com.azhar.mylyrics.activities

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.azhar.mylyrics.R
import com.azhar.mylyrics.model.ModelMain
import com.azhar.mylyrics.networking.ApiEndpoint
import kotlinx.android.synthetic.main.activity_detail_lyrics.*
import org.json.JSONException
import org.json.JSONObject

class DetailLyricsActivity : AppCompatActivity() {

    var modelMain: ModelMain? = null
    var strIdLagu: String = ""
    var strLirikLagu: String = ""
    var strNamaArtis: String = ""
    var strJudulLagu: String = ""
    var progressDialog: ProgressDialog? = null

    companion object {
        const val LIST_LYRICS = "lyrics"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_lyrics)

        progressDialog = ProgressDialog(this)
        progressDialog?.setTitle("Mohon Tunggu")
        progressDialog?.setCancelable(false)
        progressDialog?.setMessage("Sedang menampilkan lirik...")

        setSupportActionBar(toolbar)
        assert(supportActionBar != null)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        modelMain = intent.getSerializableExtra(LIST_LYRICS) as ModelMain
        if (modelMain != null) {
            strIdLagu = modelMain!!.strId
            getDetailLyrics()
        }
    }

    private fun getDetailLyrics() {
            progressDialog?.show()
            AndroidNetworking.get(ApiEndpoint.DETAILURl)
                    .addPathParameter("id", strIdLagu)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) {
                            try {
                                progressDialog?.dismiss()
                                val jsonObject = response.getJSONObject("data")

                                strNamaArtis = jsonObject.getString("artist")
                                tvNamaArtis.text = strNamaArtis

                                strJudulLagu = jsonObject.getString("songTitle")
                                tvJudulLagu.text = strJudulLagu

                                strLirikLagu = jsonObject.getString("songLyrics")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                    tvLirikLagu.text = Html.fromHtml(strLirikLagu, Html.FROM_HTML_MODE_LEGACY)
                                else {
                                    tvLirikLagu.text = Html.fromHtml(strLirikLagu)
                                }
                            } catch (e: JSONException) {
                                e.printStackTrace()
                                Toast.makeText(this@DetailLyricsActivity, "Gagal menampilkan data!",
                                        Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onError(anError: ANError) {
                            progressDialog?.dismiss()
                            Toast.makeText(this@DetailLyricsActivity, "Tidak ada jaringan internet!",
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
        }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}