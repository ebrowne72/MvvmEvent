package com.example.erikbrowne.mvvmdemo.mvvm

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import com.android.databinding.library.baseAdapters.BR

abstract class BaseMvvmActivity<B : ViewDataBinding, VM : ObservableViewModel> : AppCompatActivity() {
	protected lateinit var binding: B
	protected lateinit var viewModel: VM

	fun setModelAndView(modelClazz: Class<VM>, @LayoutRes layoutId: Int) {
		binding = DataBindingUtil.setContentView(this, layoutId)
		viewModel = ViewModelProviders.of(this).get(modelClazz)
		binding.setVariable(BR.viewModel, viewModel)
	}
}
