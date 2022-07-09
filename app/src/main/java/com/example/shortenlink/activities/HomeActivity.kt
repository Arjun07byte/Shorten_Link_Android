package com.example.shortenlink.activities

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shortenlink.R
import com.example.shortenlink.adapters.HistoryRvAdapter
import com.example.shortenlink.databinding.ActivityHomeBinding
import com.example.shortenlink.localDatabase.ShortenLinkDatabase
import com.example.shortenlink.repository.ShortenLinkRepository
import com.example.shortenlink.utils.ApiResponseState
import com.example.shortenlink.utils.Constants
import com.example.shortenlink.viewModels.ShortenLinkVMProvider
import com.example.shortenlink.viewModels.ShortenLinkViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class HomeActivity : AppCompatActivity() {
    private lateinit var myViewBinding: ActivityHomeBinding
    private lateinit var myViewModel: ShortenLinkViewModel
    private lateinit var myAdapter: HistoryRvAdapter
    private lateinit var myDialogRootView: View
    private lateinit var myBottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myViewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(myViewBinding.root)

        // Starting the animation of Shorten Link Button Animation
        startShortenButtonAnimation()

        // setting up my view Model
        setUpMyViewModel()

        // set Up my History RV adapter and initializing the History RV Adapter Object
        setUpRVAdapter()

        setUpMyBottomSheetDialog()

        // setting the Shorten Link Button Listener
        setUpShortenButtonListener()

        // Observing Live Data Changes
        observeLiveDataChanges(myDialogRootView)

        // setUpPasteButtonListener
        setUpPasteButtonListener()

        //setUpHelpButtonListener
        setUpHelpButton()
    }

    private fun setUpHelpButton() {
        myViewBinding.helpButton.setOnClickListener {
            val myIntent = Intent(this,HelpActivity::class.java)
            startActivity(myIntent)
        }
    }

    private fun setUpPasteButtonListener() {
        myViewBinding.pasteButton.setOnClickListener{
            val clipboard : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val textToPaste = clipboard.primaryClip?.getItemAt(0)?.text
            myViewBinding.enteredURL.setText(textToPaste)
        }
    }

    private fun observeLiveDataChanges(bottomDialogView: View) {
        val myDialogContentView: ConstraintLayout = bottomDialogView.findViewById(R.id.bottom_dialog_content_view)
        val myProgressBar: ProgressBar = bottomDialogView.findViewById(R.id.bottom_dialog_progress)
        val myTitleTV: TextView = bottomDialogView.findViewById(R.id.long_url_title)
        val myShortLinkTV: TextView = bottomDialogView.findViewById(R.id.shortened_link)
        val mySendButton: TextView = bottomDialogView.findViewById(R.id.share_short_link_button)

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
                        onBackPressed()
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

    private fun setUpShortenButtonListener() {
        myViewBinding.shortenButton.setOnClickListener {
            myViewModel.getMyLinkShortened(myViewBinding.enteredURL.text.toString())
            myBottomSheetDialog.show()
        }
    }

    private fun setUpMyBottomSheetDialog(){
        myBottomSheetDialog = BottomSheetDialog(
            this
        )

        myBottomSheetDialog.setContentView(myDialogRootView)
    }

    private fun setUpRVAdapter() {
        myAdapter = HistoryRvAdapter(this)
        myViewBinding.bottomSheet.findViewById<RecyclerView>(R.id.history_RV).apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    private fun setUpMyViewModel() {
        myDialogRootView = LayoutInflater.from(this).inflate(
            R.layout.bottom_dialog_view,
            myViewBinding.root,
            false
        )

        val myVMProvider = ShortenLinkVMProvider(this.application,
            ShortenLinkRepository(ShortenLinkDatabase(this))
        )
        myViewModel = ViewModelProvider(this,myVMProvider)[ShortenLinkViewModel::class.java]
    }

    private fun startShortenButtonAnimation() {
        myViewBinding.shortenButton.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.button_anim)
        )
    }

}