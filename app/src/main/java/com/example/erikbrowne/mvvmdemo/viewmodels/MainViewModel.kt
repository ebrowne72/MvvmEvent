package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.databinding.Bindable
import com.example.erikbrowne.mvvmdemo.BR
import com.example.erikbrowne.mvvmdemo.DataBindingPropDelegate
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.mvvm.ObservableViewModel
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessages
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TIMER_INTERVAL = 1000L
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
const val REQUEST_CHOOSE_FILE = 132

class MainViewModel @JvmOverloads constructor(
		application: Application,
		mainScope: CoroutineScope = MainScope(),
		private val bgDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ObservableViewModel(application, mainScope) {

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
		timerJob = mainScope.launch {
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
					val uri: Uri? = data.data
					fileUri = uri.toString()
				}
			}
		}
	}

	fun showNextFibonacci() {
		if ( fibonacciItr == null ) {
			val fibonacciSeq = sequence {
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
			val primeSeq = sequence {
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
		mainScope.launch {
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

	fun spekTest(dependency: Dependency): String {
		dependency.startSomething()
		return "value"
	}
}

interface Dependency {
	fun startSomething()
}
