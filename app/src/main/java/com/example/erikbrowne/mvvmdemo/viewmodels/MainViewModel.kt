package com.example.erikbrowne.mvvmdemo.viewmodels

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.databinding.Bindable
import android.net.Uri
import android.os.Handler
import android.os.Message
import com.android.databinding.library.baseAdapters.BR
import com.example.erikbrowne.mvvmdemo.DataBindingPropDelegate
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.SingleLiveEvent
import com.example.erikbrowne.mvvmdemo.mvvm.ObservableViewModel
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessagesEvent
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigationEvent
import java.util.concurrent.TimeUnit

class MainViewModel(application: Application) : ObservableViewModel(application) {

	var firstName = "Erik"
	var lastName = "Browne"
	var timer: String by DataBindingPropDelegate("", BR.timer)
		@Bindable get
	var fileUri: String by DataBindingPropDelegate("", BR.fileUri)
		@Bindable get
	val navigationEvent = SingleLiveEvent<ViewNavigationEvent>()
	val messageEvent = SingleLiveEvent<ViewMessagesEvent>()

	private val TIMER_MSG = 5
	private val TIMER_INTERVAL = TimeUnit.SECONDS.toMillis(1)
	private val REQUEST_CHOOSE_FILE = 132

	private val handler = @SuppressLint("HandlerLeak") object : Handler() {
		override fun handleMessage(msg: Message?) {
			if ( msg?.what == TIMER_MSG ) {
				timerVal--
				timer = when {
					timerVal < 0 -> ""
					else -> timerVal.toString()
				}
				if ( timerVal >= 0 ) {
					sendEmptyMessageDelayed(TIMER_MSG, TIMER_INTERVAL)
				}
				if ( timerVal == 0 ) {
					messageEvent.value = { this.showMessage("Timer ended") }
				}
			}
		}
	}
	private var timerVal = 0
	override fun onCleared() {
		handler.removeMessages(TIMER_MSG)
	}

	fun startTimer() {
		timerVal = 10
		timer = timerVal.toString()
		handler.sendEmptyMessageDelayed(TIMER_MSG, TIMER_INTERVAL)
	}

	fun showMessage() {
		messageEvent.value = { this.showMessage(getApplication<Application>().getString(R.string.message_text)) }
	}

	fun chooseFile() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
		intent.addCategory(Intent.CATEGORY_OPENABLE)
		navigationEvent.value = { this.startActivityForResult(intent, REQUEST_CHOOSE_FILE) }
	}

	fun processActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		when ( requestCode ) {
			REQUEST_CHOOSE_FILE -> {
				if ( resultCode == Activity.RESULT_OK && data != null ) {
					val uri: Uri = data.data
					fileUri = uri.toString()
				}
			}
		}
	}
}
