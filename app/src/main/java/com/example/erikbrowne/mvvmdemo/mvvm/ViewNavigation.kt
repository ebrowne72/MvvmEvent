package com.example.erikbrowne.mvvmdemo.mvvm

import android.content.Intent

interface ViewNavigation {

	fun startActivity(intent: Intent?)

	fun startActivityForResult(intent: Intent?, requestCode: Int)
}