package com.azhar.mylyrics.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.azhar.mylyrics.R
import com.azhar.mylyrics.model.ModelMain
import com.azhar.mylyrics.utils.OnItemClickCallback
import kotlinx.android.synthetic.main.item_list_music.view.*

/**
 * Created by Azhar Rivaldi on 26-02-2021
 */

class MainAdapter(private val context: Context, private val items: List<ModelMain>) :
        RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback?) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_music, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val modelMain = items[position]
        holder.tvNamaArtis.text = modelMain.strArtis
        holder.tvJudulLagu.text = modelMain.strTitle
        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(modelMain)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    lateinit class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNamaArtis: TextView
        var tvJudulLagu: TextView

        init {
            tvNamaArtis = itemView.tvNamaArtis
            tvJudulLagu = itemView.tvJudulLagu
        }
    }

}