package com.example.lockdown

import android.app.Application
import com.example.lockdown.data.BlockingRepository

class LockdownApp : Application() {
    lateinit var repository: BlockingRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = BlockingRepository(this)
    }
}