package com.example.shortenlink.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.shortenlink.R
import com.example.shortenlink.adapters.HistoryRvAdapter
import com.example.shortenlink.localDatabase.ShortenLinkDatabase
import com.example.shortenlink.repository.ShortenLinkRepository
import com.example.shortenlink.utils.ApiResponseState
import com.example.shortenlink.viewModels.ShortenLinkVMProvider
import com.example.shortenlink.viewModels.ShortenLinkViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyBottomSheetDialog(context: Context, private val currActivity: ShareLinkActivity): BottomSheetDialog(context) {
    override fun cancel() {
        super.cancel()
        currActivity.onBackPressed()
    }
}

class ShareLinkActivity : AppCompatActivity() {
    private lateinit var myViewModel: ShortenLinkViewModel
    private lateinit var myProgressBar: ProgressBar
    private lateinit var myTitleTV: TextView
    private lateinit var myShortLinkTV: TextView
    private lateinit var myDialogRootView: View
    private lateinit var myDialogContentView: ConstraintLayout
    private lateinit var myBottomSheetDialog: MyBottomSheetDialog
    private lateinit var mySendButton: Button
    private lateinit var myAdapter: HistoryRvAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share_link)

        setUpMyViewModel()
        setUpMyBottomSheetDialog()
        setUpVariables()
        handleIntentData()
        observeLiveDataChanges()
    }

    private fun handleIntentData() {
        myBottomSheetDialog.show()
        val receivedIntent = intent
        val givenUrl = receivedIntent.getStringExtra(Intent.EXTRA_TEXT)

        if(givenUrl == null || givenUrl.toString().isEmpty() || receivedIntent.action != Intent.ACTION_SEND){
            myProgressBar.visibility = View.GONE
            myDialogContentView.visibility = View.VISIBLE
            myTitleTV.text = "Error In Link Shortening"
            myShortLinkTV.visibility = View.GONE
            mySendButton.text = "Exit"
            mySendButton.setOnClickListener {
                onBackPressed()
            }
        } else getShortLink(givenUrl)
    }

    private fun setUpVariables() {
        myDialogContentView = myDialogRootView.findViewById(R.id.bottom_dialog_content_view)
        myProgressBar = myDialogRootView.findViewById(R.id.bottom_dialog_progress)
        myTitleTV = myDialogRootView.findViewById(R.id.long_url_title)
        myShortLinkTV = myDialogRootView.findViewById(R.id.shortened_link)
        mySendButton = myDialogRootView.findViewById(R.id.share_short_link_button)
        myAdapter = HistoryRvAdapter(this)
    }

    private fun setUpMyBottomSheetDialog() {
        myBottomSheetDialog = MyBottomSheetDialog(
            this,
            this
        )

        myDialogRootView = LayoutInflater.from(this).inflate(
            R.layout.bottom_dialog_view,
            findViewById<ConstraintLayout>(R.id.bottom_dialog_root_view),
            false
        )
        myBottomSheetDialog.setContentView(myDialogRootView)


    }

    private fun observeLiveDataChanges() {
        // observing ApiResultState's livedata changes
        // to handle the api Results accordingly
        myViewModel.apiResultList.observe(this, Observer {
            when(it) {
                is ApiResponseState.SuccessState -> {
                    myViewModel.insertShortLink(it.myData!!)
                    myProgressBar.visibility = View.GONE
                    myDialogContentView.visibility = View.VISIBLE
                    myTitleTV.text = it.myData.title
                    myShortLinkTV.text = it.myData.shortLink
                    mySendButton.text = "Share Link"
                    mySendButton.setOnClickListener {
                        val myIntent = Intent(Intent.ACTION_SEND); myIntent.type = "text/plain"
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, "Short Link")
                        myIntent.putExtra(Intent.EXTRA_TEXT, myShortLinkTV.text)
                        startActivity(Intent.createChooser(myIntent,"Choose App to Share Shorten Link"))
                    }
                }
                is ApiResponseState.ErrorState -> {
                    myProgressBar.visibility = View.GONE
                    myDialogContentView.visibility = View.VISIBLE
                    myTitleTV.text = "Error In Link Shortening"
                    myShortLinkTV.text = it.message
                    mySendButton.text = "Exit"
                    mySendButton.setOnClickListener {

                    }
                }
                else -> {
                    myProgressBar.visibility = View.VISIBLE
                    myDialogContentView.visibility = View.GONE
                }
            }
        })

        myViewModel.getAllLinks().observe(this, Observer {
            myAdapter.myDifferList.submitList(it)
        })
    }

    private fun getShortLink(givenUrl: String) {
        myViewModel.getMyLinkShortened(givenUrl)
    }

    private fun setUpMyViewModel() {
        val myVMProvider = ShortenLinkVMProvider(this.application,
            ShortenLinkRepository(ShortenLinkDatabase(this))
        )
        myViewModel = ViewModelProvider(this,myVMProvider)[ShortenLinkViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        myBottomSheetDialog.dismiss()
    }
}