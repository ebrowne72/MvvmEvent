package com.example.erikbrowne.mvvmdemo.mvvm

import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.ViewModelProviders

abstract class BaseMvvmActivity<B : ViewDataBinding, VM : ObservableViewModel> : AppCompatActivity() {
	protected lateinit var binding: B
	protected lateinit var viewModel: VM

	fun setModelAndView(modelClazz: Class<VM>, @LayoutRes layoutId: Int) {
		binding = DataBindingUtil.setContentView(this, layoutId)
		viewModel = ViewModelProviders.of(this).get(modelClazz)
		binding.setVariable(BR.viewModel, viewModel)
	}
}
