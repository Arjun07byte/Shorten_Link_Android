package com.example.shortenlink.activities

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shortenlink.R
import com.example.shortenlink.adapters.HistoryRvAdapter
import com.example.shortenlink.databinding.ActivityHomeBinding
import com.example.shortenlink.localDatabase.ShortenLinkDatabase
import com.example.shortenlink.repository.ShortenLinkRepository
import com.example.shortenlink.utils.ApiResponseState
import com.example.shortenlink.viewModels.ShortenLinkVMProvider
import com.example.shortenlink.viewModels.ShortenLinkViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var myViewBinding: ActivityHomeBinding
    private lateinit var myViewModel: ShortenLinkViewModel
    private lateinit var myAdapter: HistoryRvAdapter
    private lateinit var myDialogRootView: View
    private lateinit var myBottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        // Using View Binding for better View Handling
        super.onCreate(savedInstanceState)
        myViewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(myViewBinding.root)

        // Starting the animation of Shorten Link Button
        // as soon as the Application gets its OnCreate called
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
        // Launching the help Activity when Help Button is clicked
        // on the Home Screen
        myViewBinding.helpButton.setOnClickListener {
            val myIntent = Intent(this, HelpActivity::class.java)
            startActivity(myIntent)
        }
    }

    private fun setUpPasteButtonListener() {
        // setting up the Paste Button Listener
        // we first get an instance of CLIPBOARD System Service as our ClipBoard Manager

        // When an user clicks on the paste button the first primary clip
        // is fetched and stored in a variable which is further set
        // as the text of our URL editText
        myViewBinding.pasteButton.setOnClickListener {
            val clipboard: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val textToPaste = clipboard.primaryClip?.getItemAt(0)?.text
            myViewBinding.enteredURL.setText(textToPaste)
        }
    }

    private fun observeLiveDataChanges(bottomDialogView: View) {
        val myDialogContentView: ConstraintLayout =
            bottomDialogView.findViewById(R.id.bottom_dialog_content_view)
        val myProgressBar: ProgressBar = bottomDialogView.findViewById(R.id.bottom_dialog_progress)
        val myTitleTV: TextView = bottomDialogView.findViewById(R.id.long_url_title)
        val myShortLinkTV: TextView = bottomDialogView.findViewById(R.id.shortened_link)
        val mySendButton: TextView = bottomDialogView.findViewById(R.id.share_short_link_button)

        // observing ApiResultState's livedata changes
        // to check current status of our request sent
        myViewModel.apiResultList.observe(this) {
            when (it) {
                is ApiResponseState.SuccessState -> {
                    myViewModel.insertShortLink(it.myData!!)
                    myProgressBar.visibility = View.GONE
                    myDialogContentView.visibility = View.VISIBLE
                    myTitleTV.text = it.myData.title
                    myShortLinkTV.text = it.myData.shortLink
                    mySendButton.text = getString(R.string.share_link_text)

                    // Creating an intent to share to shortened link
                    // and giving user a chooser to select apps from
                    mySendButton.setOnClickListener {
                        val myIntent = Intent(Intent.ACTION_SEND); myIntent.type = "text/plain"
                        myIntent.putExtra(Intent.EXTRA_SUBJECT, "Short Link")
                        myIntent.putExtra(Intent.EXTRA_TEXT, myShortLinkTV.text)
                        startActivity(
                            Intent.createChooser(
                                myIntent,
                                "Choose App to Share Shorten Link"
                            )
                        )
                    }
                }
                is ApiResponseState.ErrorState -> {
                    myProgressBar.visibility = View.GONE
                    myDialogContentView.visibility = View.VISIBLE
                    myTitleTV.text = getString(R.string.error_text)
                    myShortLinkTV.text = it.message
                    mySendButton.text = getString(R.string.exit_text)
                    mySendButton.setOnClickListener{
                        myBottomSheetDialog.dismiss()
                    }
                }
                else -> {
                    myProgressBar.visibility = View.VISIBLE
                    myDialogContentView.visibility = View.GONE
                }
            }
        }

        // observing the links which the user has shortened
        // and updating the list accordingly
        myViewModel.getAllLinks().observe(this) {
            myAdapter.myDifferList.submitList(it)
        }
    }

    // Assigning the onClickListener to shorten button
    // clicking which send a request to viewModel to shorten the Link
    // and open the bottom sheet dialog to let the user interact with
    private fun setUpShortenButtonListener() {
        myViewBinding.shortenButton.setOnClickListener {
            myViewModel.getMyLinkShortened(myViewBinding.enteredURL.text.toString())
            myBottomSheetDialog.show()
        }
    }

    private fun setUpMyBottomSheetDialog() {
        // setting up the Bottom Sheet Dialog
        // and its views
        myBottomSheetDialog = BottomSheetDialog(
            this
        )

        myDialogRootView = LayoutInflater.from(this).inflate(
            R.layout.bottom_dialog_view,
            myViewBinding.root,
            false
        )
        myBottomSheetDialog.setContentView(myDialogRootView)
    }

    private fun setUpRVAdapter() {
        // setting up the RV Adapter for previous shorten links RV
        // contained under the Modal Bottom Sheet
        myAdapter = HistoryRvAdapter(this)
        myViewBinding.bottomSheet.findViewById<RecyclerView>(R.id.history_RV).apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    private fun setUpMyViewModel() {
        // initializing our ViewModel
        val myVMProvider = ShortenLinkVMProvider(
            this.application,
            ShortenLinkRepository(ShortenLinkDatabase(this))
        )
        myViewModel = ViewModelProvider(this, myVMProvider)[ShortenLinkViewModel::class.java]
    }

    private fun startShortenButtonAnimation() {
        // loading the button animation to the Home Page Shorten Button
        myViewBinding.shortenButton.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.button_anim)
        )
    }

    override fun onResume() {
        super.onResume()
        setTheme(R.style.Theme_ShortenLink)
    }
}