package com.example.erikbrowne.mvvmdemo

import java.util.concurrent.Executor

object DirectExecutor : Executor {
	override fun execute(command: Runnable?) {
		command?.run()
	}
}
