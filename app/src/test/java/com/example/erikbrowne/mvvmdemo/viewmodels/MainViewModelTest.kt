package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Application
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessagesEvent
import com.nhaarman.mockito_kotlin.mock
import org.junit.Rule
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MainViewModelTest {

	@Rule
	@JvmField
	val rule = InstantTaskExecutorRule()

	lateinit var viewModel: MainViewModel

	@BeforeEach
	fun beforeEachTest() {
		viewModel = MainViewModel(mock<Application>())
	}

	@Test
	fun `showMessage sets message event`() {
		viewModel.showMessage()

		assertTrue(viewModel.messageEvent.value is ViewMessagesEvent)
	}
}
