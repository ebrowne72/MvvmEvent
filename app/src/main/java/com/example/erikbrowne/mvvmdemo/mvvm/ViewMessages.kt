package com.example.erikbrowne.mvvmdemo.mvvm

typealias ViewMessagesEvent = ViewMessages.() -> Unit

interface ViewMessages {
	fun showMessage(msg: String)
}
