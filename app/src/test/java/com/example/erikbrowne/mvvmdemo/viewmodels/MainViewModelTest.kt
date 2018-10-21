package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Activity
import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Intent
import android.net.Uri
import com.example.erikbrowne.mvvmdemo.R
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import kotlinx.coroutines.experimental.CancellableContinuation
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.Delay
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class TestDirectDispatcher : CoroutineDispatcher(), Delay {
	override fun scheduleResumeAfterDelay(time: Long, unit: TimeUnit, continuation: CancellableContinuation<Unit>) {
		continuation.resume(Unit)
	}

	override fun dispatch(context: CoroutineContext, block: Runnable) {
		block.run()
	}

}

internal class MainViewModelTest {

	@Rule
	@JvmField
	val rule = InstantTaskExecutorRule()

	private lateinit var viewModel: MainViewModel

	private val message = "message"

	private val application = mock<Application> {
		on { getString(R.string.message_text) } doReturn message
	}

	private inline fun <reified T : Any> getAndProcessEvent(event: LiveMessageEvent<T>): T {
		val eventValue = event.value
		val receiverObject = mock<T>()
		if ( eventValue != null ) {
			receiverObject.eventValue()
		}
		return receiverObject
	}

	@Before
	fun beforeEachTest() {
		viewModel = MainViewModel(application, TestDirectDispatcher(), TestDirectDispatcher())
	}

	@Test
	fun `showMessage sends message event and shows message`() {
		viewModel.showMessage()

		val viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage(message)
	}

	@Test
	fun `chooseFile sends nav event and starts activity`() {
		viewModel.chooseFile()

		val viewNavObj = getAndProcessEvent(viewModel.navigationEvent)
		verify(viewNavObj).startActivityForResult(check {
			assertEquals(Intent.ACTION_GET_CONTENT, it.action)
			assertEquals("image/*", it.type)
		}, any())
	}

	@Test
	fun `processActivityResult success sets URI`() {
		val uriStr = "content://something"
		val uri = Uri.parse(uriStr)
		val intent = mock<Intent> {
			on { data } doReturn uri
		}

		viewModel.processActivityResult(REQUEST_CHOOSE_FILE, Activity.RESULT_OK, intent)

		assertEquals(uriStr, viewModel.fileUri)
	}

	@Test
	fun `processActivityResult canceled does nothing`() {
		viewModel.processActivityResult(REQUEST_CHOOSE_FILE, Activity.RESULT_CANCELED, null)

		assertEquals("", viewModel.fileUri)
	}

	/*
	@Test
	fun `startTimer starts timer and shows message`() {
		val testCoroutineContext = TestCoroutineContext()
		val localViewModel = MainViewModel(application, testCoroutineContext, testCoroutineContext)
		localViewModel.startTimer()

		testCoroutineContext.advanceTimeTo(500, TimeUnit.MILLISECONDS)
		assertEquals("Timer isn't 10", "10", localViewModel.timer)

		testCoroutineContext.advanceTimeTo(5500, TimeUnit.MILLISECONDS)
		assertEquals("Timer isn't 5", "5", localViewModel.timer)

		testCoroutineContext.advanceTimeTo(9500, TimeUnit.MILLISECONDS)
		assertEquals("Timer isn't 1", "1", localViewModel.timer)

		var viewMsgsObj = getAndProcessEvent(localViewModel.messagesEvent)
		verify(viewMsgsObj, never()).showMessage(anyString())

		testCoroutineContext.advanceTimeTo(10500, TimeUnit.MILLISECONDS)
		assertEquals("Timer isn't 0", "0", localViewModel.timer)
		viewMsgsObj = getAndProcessEvent(localViewModel.messagesEvent)
		verify(viewMsgsObj).showMessage("Timer ended")

		testCoroutineContext.advanceTimeTo(11500, TimeUnit.MILLISECONDS)
		assertEquals("Timer not empty","", localViewModel.timer)
	}

	@Test
	fun `disposing ViewModel cancels timer`() {
		val testCoroutineContext = TestCoroutineContext()
		val localViewModel = MainViewModel(application, testCoroutineContext, testCoroutineContext)
		val viewModelStore = ViewModelStore()
		val viewModelProvider = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
			@Suppress("UNCHECKED_CAST")
			override fun <T : ViewModel?> create(modelClass: Class<T>): T {
				return localViewModel as T
			}
		})
		viewModelProvider.get("key", MainViewModel::class.java)
		localViewModel.startTimer()

		testCoroutineContext.advanceTimeTo(500, TimeUnit.MILLISECONDS)
		assertEquals("Timer isn't 10", "10", localViewModel.timer)

		viewModelStore.clear()
		testCoroutineContext.advanceTimeTo(1500, TimeUnit.MILLISECONDS)
		assertEquals("Timer isn't still 10", "10", localViewModel.timer)
	}
*/

	@Test
	fun `showNextFibonacci shows Fibonacci numbers`() {
		assertEquals("", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("2", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("3", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("5", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("8", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("13", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("21", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("34", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("55", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("89", viewModel.fibonacci)
		viewModel.showNextFibonacci()
		assertEquals("144", viewModel.fibonacci)
	}

	@Test
	fun `showNextPrime shows primes`() {
		assertEquals("", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("2", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("3", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("5", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("7", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("11", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("13", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("17", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("19", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("23", viewModel.prime)
		viewModel.showNextPrime()
		assertEquals("29", viewModel.prime)
	}

	@Test
	fun `doSomethingAsync waits then shows message`() {
		viewModel.doSomethingAsync()

		val viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage("Coroutine is done")
	}

	@Test
	fun `asyncOutside waits and returns sum`() = runBlocking {
		val localVieModel = MainViewModel(application, TestDirectDispatcher(), Dispatchers.Default)
		assertEquals(111, localVieModel.asyncOutside())
	}
}
