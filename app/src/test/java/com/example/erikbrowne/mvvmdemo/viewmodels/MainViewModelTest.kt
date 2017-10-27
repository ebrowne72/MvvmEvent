package com.example.erikbrowne.mvvmdemo.viewmodels

import android.app.Application
import com.nhaarman.mockito_kotlin.mock
import org.junit.jupiter.api.Test

internal class MainViewModelTest {

	@Test
	fun foo() {
		MainViewModel(mock<Application>())
	}
}