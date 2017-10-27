package com.example.erikbrowne.mvvmdemo

import com.example.erikbrowne.mvvmdemo.mvvm.ObservableViewModel
import kotlin.reflect.KProperty

class DataBindingPropDelegate<T : Any>(private var value: T, private val fieldId: Int) {

	operator fun getValue(thisRef: ObservableViewModel, property: KProperty<*>): T {
		return value
	}

	operator fun setValue(thisRef: ObservableViewModel, property: KProperty<*>, value: T) {
		this.value = value
		thisRef.notifyPropertyChanged(fieldId)
	}
}