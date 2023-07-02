package com.example.erikbrowne.mvvmdemo.activities

import android.os.Bundle
import android.widget.Button
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.erikbrowne.mvvmdemo.R
import com.example.erikbrowne.mvvmdemo.databinding.ActivityMainBinding
import com.example.erikbrowne.mvvmdemo.mvvm.BaseMvvmActivity
import com.example.erikbrowne.mvvmdemo.mvvm.ViewMessages
import com.example.erikbrowne.mvvmdemo.mvvm.ViewNavigation
import com.example.erikbrowne.mvvmdemo.viewmodels.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : BaseMvvmActivity<ActivityMainBinding, MainViewModel>(), ViewMessages, ViewNavigation {

	private val picturePickerResultLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
		viewModel.onPicturePicked(it)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		setModelAndView(MainViewModel::class.java, R.layout.activity_main)
		viewModel.messagesEvent.setEventReceiver(this, this)
		viewModel.navigationEvent.setEventReceiver(this, this)
		findViewById<Button>(R.id.startCollectButton).setOnClickListener {
			lifecycleScope.launch {
				repeatOnLifecycle(Lifecycle.State.RESUMED) {
					viewModel.valueFlow?.collect {
						viewModel.flowValues += "$it "
					}
				}
			}
		}
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

	override fun startPicturePicker() {
		picturePickerResultLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
	}
}
