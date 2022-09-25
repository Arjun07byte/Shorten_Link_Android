package com.example.shortenlink.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.google.android.material.snackbar.Snackbar

class HistoryRvAdapter(
    val context: Context
) : RecyclerView.Adapter<HistoryRvAdapter.HistoryViewModel>() {
    inner class HistoryViewModel(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sharedTitle: TextView = itemView.findViewById(R.id.shared_link_title)
        val shortenedLink: TextView = itemView.findViewById(R.id.shorten_link)
        val copyButton: ImageButton = itemView.findViewById(R.id.copy_short_link)
    }

    // using Diff Util to handle all the changes in the list
    private val differCallBack = object : DiffUtil.ItemCallback<ShortenUrl>() {
        override fun areItemsTheSame(oldItem: ShortenUrl, newItem: ShortenUrl): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: ShortenUrl, newItem: ShortenUrl): Boolean {
            return oldItem == newItem
        }
    }

    val myDifferList = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewModel {
        return HistoryViewModel(
            LayoutInflater.from(parent.context).inflate(
                R.layout.search_history_rv,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return myDifferList.currentList.size
    }

    override fun onBindViewHolder(holder: HistoryViewModel, position: Int) {
        val currShortUrl = myDifferList.currentList[position]
        holder.apply {
            sharedTitle.text = currShortUrl.title
            shortenedLink.text = currShortUrl.shortLink
            copyButton.setOnClickListener { copyOnClick(currShortUrl.shortLink, itemView) }
        }
    }

    // each Item will have a copy button clicking on which
    // let user copy the shortened Link in the clipboard
    private fun copyOnClick(givenTextToCopy: String, itemView: View) {
        val myClipboard: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val myClipData = ClipData.newPlainText("Short Url", givenTextToCopy)
        myClipboard.setPrimaryClip(myClipData)

        Snackbar.make(itemView,"Link Copied",Snackbar.LENGTH_LONG).show()
    }
}