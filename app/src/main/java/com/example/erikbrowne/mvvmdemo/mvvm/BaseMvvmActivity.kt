package com.example.erikbrowne.mvvmdemo.mvvm

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProvider

abstract class BaseMvvmActivity<B : ViewDataBinding, VM : ObservableViewModel> : AppCompatActivity() {
	protected lateinit var binding: B
	protected lateinit var viewModel: VM

	fun setModelAndView(modelClazz: Class<VM>, @LayoutRes layoutId: Int) {
		binding = DataBindingUtil.setContentView(this, layoutId)
		viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(modelClazz)
		binding.setVariable(BR.viewModel, viewModel)
	}
}
