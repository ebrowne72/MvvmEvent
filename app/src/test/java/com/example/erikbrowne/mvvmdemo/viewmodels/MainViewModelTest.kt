package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.erikbrowne.mvvmdemo.R
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString


@ExperimentalCoroutinesApi
internal class MainViewModelTest {

	@Rule
	@JvmField
	val rule = InstantTaskExecutorRule()

	private val testDispatcher = TestCoroutineDispatcher()
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
		viewModel = MainViewModel(application, testDispatcher, testDispatcher)
	}

	@After
	fun afterEachTest() {
		testDispatcher.cleanupTestCoroutines()
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

	@Test
	fun `startTimer starts timer and shows message`() {
		viewModel.startTimer()

		testDispatcher.advanceTimeBy(500)
		assertEquals("Timer isn't 10", "10", viewModel.timer)

		testDispatcher.advanceTimeBy(5000)
		assertEquals("Timer isn't 5", "5", viewModel.timer)

		testDispatcher.advanceTimeBy(4000)
		assertEquals("Timer isn't 1", "1", viewModel.timer)

		var viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj, never()).showMessage(anyString())

		testDispatcher.advanceTimeBy(1000)
		assertEquals("Timer isn't 0", "0", viewModel.timer)
		viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage("Timer ended")

		testDispatcher.advanceTimeBy(1000)
		assertEquals("Timer not empty","", viewModel.timer)
	}

	@Test
	fun `disposing ViewModel cancels timer`() {
		val viewModelStore = ViewModelStore()
		val viewModelProvider = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
			@Suppress("UNCHECKED_CAST")
			override fun <T : ViewModel?> create(modelClass: Class<T>): T {
				return viewModel as T
			}
		})
		viewModelProvider.get("key", MainViewModel::class.java)
		viewModel.startTimer()

		testDispatcher.advanceTimeBy(500)
		assertEquals("Timer isn't 10", "10", viewModel.timer)

		viewModelStore.clear()
		testDispatcher.advanceTimeBy(1000)
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
	fun `doSomethingAsync waits then shows message`() {
		viewModel.doSomethingAsync()
		testDispatcher.advanceUntilIdle()

		val viewMsgsObj = getAndProcessEvent(viewModel.messagesEvent)
		verify(viewMsgsObj).showMessage("Coroutine is done")
	}

	@Test
	fun `asyncOutside waits and returns sum`() = testDispatcher.runBlockingTest {
		val value = viewModel.asyncOutside()
		assertEquals(111, value)
	}
}
