package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Activity
import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.content.Intent
import android.net.Uri
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessages
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigation
import com.nhaarman.mockito_kotlin.*
import kotlinx.coroutines.experimental.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class TestUiContext : CoroutineDispatcher(), Delay {
	override fun scheduleResumeAfterDelay(time: Long, unit: TimeUnit, continuation: CancellableContinuation<Unit>) {
		println("scheduleresume")
	}

	suspend override fun delay(time: Long, unit: TimeUnit) {
		println("delay")
	}

	override fun dispatch(context: CoroutineContext, block: Runnable) {
		println("dispatch")
		CommonPool.dispatch(context, block)
	}

}

internal class MainViewModelTest {

	@Rule
	@JvmField
	val rule = InstantTaskExecutorRule()

	private lateinit var viewModel: MainViewModel

	private val uiContext = CommonPool//TestUiContext()

	private val message = "message"

	private val application = mock<Application> {
		on { getString(R.string.message_text) } doReturn message
	}

	@Before
	fun beforeEachTest() {
		viewModel = MainViewModel(application, uiContext)
	}

	@Test
	fun `showMessage sends message event and shows message`() {
		viewModel.showMessage()

		val event = viewModel.messagesEvent.value
		assertNotNull(event)
		val viewMsgsObj = mock<ViewMessages>()
		// the 'if' performs a smart cast
		if ( event != null) {
			viewMsgsObj.event()
		}
		verify(viewMsgsObj).showMessage(message)
	}

	@Test
	fun `chooseFile sends nav event and starts activity`() {
		viewModel.chooseFile()

		val event = viewModel.navigationEvent.value
		assertNotNull(event)
		val viewNavObj = mock<ViewNavigation>()
		// the 'if' performs a smart cast
		if ( event != null) {
			viewNavObj.event()
		}
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
	fun `startTimer starts timer`() {
		runBlocking {
			viewModel.startTimer()
			viewModel.timerJob?.join()
		}
	}
}
