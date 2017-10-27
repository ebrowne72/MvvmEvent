package com.example.erikbrowne.mvvmdemo.mvvm

import android.content.Intent

typealias ViewNavigationEvent = ViewNavigation.() -> Unit

interface ViewNavigation {

	fun startActivity(intent: Intent?)

	fun startActivityForResult(intent: Intent?, requestCode: Int)
}
