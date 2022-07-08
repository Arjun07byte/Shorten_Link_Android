package com.example.shortenlink

import android.content.ClipboardManager
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shortenlink.adapters.HistoryRvAdapter
import com.example.shortenlink.databinding.ActivityHomeBinding
import com.example.shortenlink.localDatabase.ShortenLinkDatabase
import com.example.shortenlink.repository.ShortenLinkRepository
import com.example.shortenlink.utils.ApiResponseState
import com.example.shortenlink.viewModels.ShortenLinkVMProvider
import com.example.shortenlink.viewModels.ShortenLinkViewModel
import com.google.android.material.snackbar.Snackbar

class HomeActivity : AppCompatActivity() {
    private lateinit var myViewBinding: ActivityHomeBinding
    private lateinit var myViewModel: ShortenLinkViewModel
    private lateinit var myAdapter: HistoryRvAdapter

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

        // setting the Shorten Link Button Listener
        setUpShortenButtonListener()

        // Observing Live Data Changes
        observeLiveDataChanges()

        // setUpPasteButtonListener
        setUpPasteButtonListener()
    }

    private fun setUpPasteButtonListener() {
        myViewBinding.pasteButton.setOnClickListener{
            val clipboard : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val textToPaste = clipboard.primaryClip?.getItemAt(0)?.text
            myViewBinding.enteredURL.setText(textToPaste)
        }
    }

    private fun observeLiveDataChanges() {
        // observing ApiResultState's livedata changes
        // to handle the api Results accordingly
        myViewModel.apiResultList.observe(this, Observer {
            if(it is ApiResponseState.SuccessState) {
                myViewModel.insertShortLink(it.myData!!)
            } else if(it is ApiResponseState.ErrorState){
                Snackbar.make(myViewBinding.root,it.message!!,Snackbar.LENGTH_LONG).show()
            }
        })

        myViewModel.getAllLinks().observe(this, Observer {
            myAdapter.myDifferList.submitList(it)
        })
    }

    private fun setUpShortenButtonListener() {
        myViewBinding.shortenButton.setOnClickListener {
            myViewModel.getMyLinkShortened(myViewBinding.enteredURL.text.toString())
        }
    }

    private fun setUpRVAdapter() {
        myAdapter = HistoryRvAdapter(this)
        myViewBinding.bottomSheet.findViewById<RecyclerView>(R.id.history_RV).apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(this.context)
        }
    }

    private fun setUpMyViewModel() {
        val myVMProvider = ShortenLinkVMProvider(this.application,
            ShortenLinkRepository(ShortenLinkDatabase(this))
        )
        myViewModel = ViewModelProvider(this,myVMProvider)[ShortenLinkViewModel::class.java]
    }

    private fun startShortenButtonAnimation() {
        myViewBinding.shortenButton.startAnimation(
            AnimationUtils.loadAnimation(this,R.anim.button_anim)
        )
    }

}