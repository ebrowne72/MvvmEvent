package com.example.erikbrowne.mvvmdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.databinding.ActivityMainBinding
import com.example.erikbrowne.mvvmdemo.mvvm.BaseMvvmActivity
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessages
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigation
import com.example.erikbrowne.mvvmdemo.viewmodels.MainViewModel

class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>(), ViewMessages, ViewNavigation {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		setModelAndView(MainViewModel::class.java, R.layout.activity_main)
		viewModel.messagesEvent.setEventReceiver(this, this)
		viewModel.navigationEvent.setEventReceiver(this, this)
    }

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		viewModel.processActivityResult(requestCode, resultCode, data)
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
