package com.example.erikbrowne.mvvmdemo.activities

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.databinding.ActivityMainBinding
import com.example.erikbrowne.mvvmdemo.mvvm.BaseMvvmActivity
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessages
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessagesEvent
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigation
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigationEvent
import com.example.erikbrowne.mvvmdemo.viewmodels.MainViewModel

class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>(), ViewMessages, ViewNavigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		setModelAndView(MainViewModel::class.java, R.layout.activity_main)
		model.messageEvent.observe(this, Observer<ViewMessagesEvent> { event ->
			event?.invoke(this)
		})
		model.navigationEvent.observe(this, Observer<ViewNavigationEvent> { event ->
			event?.invoke(this)
		})
    }

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		model.processActivityResult(requestCode, resultCode, data)
		super.onActivityResult(requestCode, resultCode, data)
	}

	override fun showMessage(msg: String) {
		val builder = AlertDialog.Builder(this)
		builder.setMessage(msg)
				.setTitle(getString(R.string.message_title))
				.setPositiveButton(android.R.string.ok) { dialog, _ ->
					dialog.dismiss()
				}
		builder.show()
	}
}
