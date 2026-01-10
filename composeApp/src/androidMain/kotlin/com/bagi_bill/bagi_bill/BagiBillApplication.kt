package com.bagi_bill.bagi_bill

import android.app.Application
import com.bagi_bill.bagi_bill.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class BagiBillApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@BagiBillApplication)
            modules(appModules)
        }
    }
}

