package com.example.erikbrowne.mvvmdemo.viewmodels

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import com.example.erikbrowne.mvvmdemo.SingleLiveEvent

class LiveMessageEvent<T> : SingleLiveEvent<(T.() -> Unit)?>() {

	fun setEventReceiver(owner: LifecycleOwner, receiver: T) {
		observe(owner, Observer { event ->
			if ( event != null ) {
				receiver.event()
			}
		})
	}

	fun sendEvent(event: (T.() -> Unit)?) {
		value = event
	}
}
