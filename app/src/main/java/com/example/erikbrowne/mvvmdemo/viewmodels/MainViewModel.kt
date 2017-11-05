package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.databinding.Bindable
import android.net.Uri
import android.support.annotation.VisibleForTesting
import com.android.databinding.library.baseAdapters.BR
import com.example.erikbrowne.mvvmdemo.DataBindingPropDelegate
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.SingleLiveEvent
import com.example.erikbrowne.mvvmdemo.mvvm.ObservableViewModel
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessagesEvent
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigationEvent
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

const private val TIMER_INTERVAL = 1000L
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
const val REQUEST_CHOOSE_FILE = 132

class MainViewModel(application: Application) : ObservableViewModel(application) {

	var firstName = "Erik"
	var lastName = "Browne"
	var timer: String by DataBindingPropDelegate("", BR.timer)
		@Bindable get
	var fileUri: String by DataBindingPropDelegate("", BR.fileUri)
		@Bindable get
	val navigationEvent = SingleLiveEvent<ViewNavigationEvent>()
	val messageEvent = SingleLiveEvent<ViewMessagesEvent>()

	private var timerJob: Job? = null

	override fun onCleared() {
		timerJob?.cancel()
	}

	fun startTimer() {
		timerJob?.cancel()
		timerJob = launch(UI) {
			for ( i in 10 downTo 0 ) {
				timer = i.toString()
				if ( i == 0 ) {
					messageEvent.value = { showMessage("Timer ended") }
				}
				delay(TIMER_INTERVAL)
			}
			timer = ""
		}
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
