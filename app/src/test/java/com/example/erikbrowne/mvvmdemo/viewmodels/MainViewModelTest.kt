package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Application
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.erikbrowne.mvvmdemo.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
internal class MainViewModelTest {

	@Rule
	@JvmField
	val rule = InstantTaskExecutorRule()

	private val testScope = TestScope()
	private val testDispatcher = StandardTestDispatcher(testScope.testScheduler)
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
		viewModel = MainViewModel(application, testScope, testDispatcher)
	}

	@Test
	fun `showMessage sends message event and shows message`() {
		viewModel.showMessage()

		val viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage(message)
	}

	@Test
	fun `chooseFile sends nav event and starts picture picker`() {
		viewModel.chooseFile()

		val viewNavObj = getAndProcessEvent(viewModel.navigationEvent)
		verify(viewNavObj).startPicturePicker()
	}

	@Test
	fun `onPicturePicked success sets URI`() {
		val uriStr = "content://something"
		val uri = Uri.parse(uriStr)

		viewModel.onPicturePicked(uri)

		assertEquals(uriStr, viewModel.fileUri)
	}

	@Test
	fun `onPicturePicked canceled does nothing`() {
		viewModel.onPicturePicked(null)

		assertEquals("", viewModel.fileUri)
	}

	@Test
	fun `startTimer starts timer and shows message`() = testScope.runTest {
		viewModel.startTimer()

		advanceTimeBy(500)
		assertEquals("Timer isn't 10", "10", viewModel.timer)

		advanceTimeBy(5000)
		assertEquals("Timer isn't 5", "5", viewModel.timer)

		advanceTimeBy(4000)
		assertEquals("Timer isn't 1", "1", viewModel.timer)

		var viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj, never()).showMessage(anyString())

		advanceTimeBy(1000)
		assertEquals("Timer isn't 0", "0", viewModel.timer)
		viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage("Timer ended")

		advanceTimeBy(1000)
		assertEquals("Timer not empty","", viewModel.timer)
	}

	@Test
	fun `disposing ViewModel cancels timer`() {
		val viewModelStore = ViewModelStore()
		val viewModelProvider = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
			@Suppress("UNCHECKED_CAST")
			override fun <T : ViewModel> create(modelClass: Class<T>): T {
				return viewModel as T
			}
		})
		viewModelProvider["key", MainViewModel::class.java]
		viewModel.startTimer()

		testScope.advanceTimeBy(500)
		assertEquals("Timer isn't 10", "10", viewModel.timer)

		viewModelStore.clear()
		testScope.advanceTimeBy(1000)
		assertEquals("Timer isn't still 10", "10", viewModel.timer)
	}

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
	fun `doSomethingAsync waits then shows message`() = testScope.runTest {
		viewModel.doSomethingAsync()
		advanceUntilIdle()

		val viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage("Coroutine is done")
	}

	@Test
	fun `asyncOutside waits and returns sum`() = testScope.runTest {
		val value = viewModel.asyncOutside()
		assertEquals(111, value)
	}
}
