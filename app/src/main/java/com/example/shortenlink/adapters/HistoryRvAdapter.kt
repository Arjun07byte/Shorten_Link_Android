package com.example.shortenlink.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shortenlink.R
import com.example.shortenlink.models.ShortenUrl

class HistoryRvAdapter : RecyclerView.Adapter<HistoryRvAdapter.HistoryViewModel>() {
    inner class HistoryViewModel(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sharedTitle: TextView = itemView.findViewById(R.id.shared_link_title)
        val shortenedLink: TextView = itemView.findViewById(R.id.shorten_link)
        val copyButton: ImageButton = itemView.findViewById(R.id.copy_short_link)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewModel {
        return HistoryViewModel(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_history_rv,
                parent,
                false
            )
        )
    }

    private val differCallBack = object  : DiffUtil.ItemCallback<ShortenUrl>(){
        override fun areItemsTheSame(oldItem: ShortenUrl, newItem: ShortenUrl): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ShortenUrl, newItem: ShortenUrl): Boolean {
            return oldItem == newItem
        }
    }

    private val myDifferList = AsyncListDiffer(this,differCallBack)

    override fun getItemCount(): Int {
        return myDifferList.currentList.size
    }

    override fun onBindViewHolder(holder: HistoryViewModel, position: Int) {
        val currShortUrl = myDifferList.currentList[position]
    }

}