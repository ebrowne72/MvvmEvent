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
import com.example.erikbrowne.mvvmdemo.mvvm.ObservableViewModel
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessages
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigation
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.Main
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import kotlin.coroutines.experimental.buildSequence

private const val TIMER_INTERVAL = 1000L
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
const val REQUEST_CHOOSE_FILE = 132

class MainViewModel @JvmOverloads constructor(
		application: Application,
		mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
		private val bgDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ObservableViewModel(application, mainDispatcher) {

	var firstName = "Erik"
	var lastName = "Browne"
	var timer: String by DataBindingPropDelegate("", BR.timer)
		@Bindable get
	var fileUri: String by DataBindingPropDelegate("", BR.fileUri)
		@Bindable get
	var fibonacci: String by DataBindingPropDelegate("", BR.fibonacci)
		@Bindable get
	var prime: String by DataBindingPropDelegate("", BR.prime)
		@Bindable get
	val navigationEvent = LiveMessageEvent<ViewNavigation>()
	val messagesEvent = LiveMessageEvent<ViewMessages>()

	private var fibonacciItr: Iterator<Int>? = null
	private var primeItr: Iterator<Int>? = null
	private var timerJob: Job? = null

	fun startTimer() {
		timerJob?.cancel()
		timerJob = launch {
			for ( i in 10 downTo 0 ) {
				timer = i.toString()
				if ( i == 0 ) {
					messagesEvent.sendEvent { showMessage("Timer ended") }
				}
				delay(TIMER_INTERVAL)
			}
			timer = ""
		}
	}

	fun showMessage() {
		messagesEvent.sendEvent { showMessage(getApplication<Application>().getString(R.string.message_text)) }
	}

	fun chooseFile() {
		val intent = Intent(Intent.ACTION_GET_CONTENT)
		intent.type = "image/*"
		intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
		intent.addCategory(Intent.CATEGORY_OPENABLE)
		navigationEvent.sendEvent { startActivityForResult(intent, REQUEST_CHOOSE_FILE) }
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

	fun showNextFibonacci() {
		if ( fibonacciItr == null ) {
			val fibonacciSeq = buildSequence {
				var prevPrev = 1
				var prev = 1

				while (true) {
					val newValue = prevPrev + prev
					yield(newValue)
					prevPrev = prev
					prev = newValue
				}
			}

			fibonacciItr = fibonacciSeq.iterator()
		}

		fibonacci = fibonacciItr?.next().toString()
	}

	fun showNextPrime() {
		if ( primeItr == null ) {
			val primeSeq = buildSequence {
				val primeList = mutableListOf<Int>()

				// yield the first prime
				yield(2)

				// test odd numbers for primeness
				var candidate = 1
				while (true) {
					candidate += 2
					if ( primeList.any { candidate % it == 0 } ) continue
					primeList.add(candidate)
					yield(candidate)
				}
			}

			primeItr = primeSeq.iterator()
		}

		prime = primeItr?.next().toString()
	}

	fun doSomethingAsync() {
		launch {
			val data = getDataFromNet()
			messagesEvent.sendEvent { showMessage(data) }
		}

	}

	private suspend fun getDataFromNet(): String = withContext(bgDispatcher) {
		delay(5000)
		"Coroutine is done"
	}

	suspend fun asyncOutside(): Int {
		lateinit var deferred1: Deferred<Int>
		lateinit var deferred2: Deferred<Int>
		coroutineScope {
			println("start scope")
			deferred1 = async(bgDispatcher) {
				delay(2000)
				println("async 1 complete")
				42
			}
			deferred2 = async(bgDispatcher) {
				delay(1000)
				println("async 2 complete")
				69
			}
			println("end scope")
		}

		println("all done")
		return deferred1.await() + deferred2.await()
	}

}
