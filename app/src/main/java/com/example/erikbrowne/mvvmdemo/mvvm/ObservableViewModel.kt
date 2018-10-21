package com.example.erikbrowne.mvvmdemo.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.annotation.CallSuper
import kotlinx.coroutines.experimental.CoroutineDispatcher
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlin.coroutines.experimental.CoroutineContext

open class ObservableViewModel(application: Application, private val mainDispatcher: CoroutineDispatcher)
	: AndroidViewModel(application), Observable, CoroutineScope {

	private val job = Job()
	override val coroutineContext: CoroutineContext
		get() = job + mainDispatcher

	@CallSuper
	override fun onCleared() {
		job.cancel()
	}

	/**
	 * Copied from BaseObservable
	 */

	@Transient
	private var mCallbacks: PropertyChangeRegistry? = null

	override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		synchronized(this) {
			if (mCallbacks == null) {
				mCallbacks = PropertyChangeRegistry()
			}
		}
		mCallbacks?.add(callback)
	}

	override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
		synchronized(this) {
			if (mCallbacks == null) {
				return
			}
		}
		mCallbacks?.remove(callback)
	}

	/**
	 * Notifies listeners that all properties of this instance have changed.
	 */
	fun notifyChange() {
		synchronized(this) {
			if (mCallbacks == null) {
				return
			}
		}
		mCallbacks?.notifyCallbacks(this, 0, null)
	}

	/**
	 * Notifies listeners that a specific property has changed. The getter for the property
	 * that changes should be marked with [Bindable] to generate a field in
	 * `BR` to be used as `fieldId`.
	 *
	 * @param fieldId The generated BR id for the Bindable field.
	 */
	fun notifyPropertyChanged(fieldId: Int) {
		synchronized(this) {
			if (mCallbacks == null) {
				return
			}
		}
		mCallbacks?.notifyCallbacks(this, fieldId, null)
	}

}
