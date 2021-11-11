package ru.smartro.inventory.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractAct : AppCompatActivity() {
    protected val log: Logger = LoggerFactory.getLogger("${this::class.simpleName}")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.debug("onCreate")
    }

    override fun onPause() {
        super.onPause()
        log.debug("onPause")
    }

    override fun onResume() {
        super.onResume()
        log.debug("onResume")
    }

    override fun onStart() {
        super.onStart()
        log.debug("onStart")
    }

    override fun onStop() {
        super.onStop()
        log.debug("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        log.debug("onDestroy")
    }
}