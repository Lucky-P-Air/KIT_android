package com.example.kit

import android.app.Application
import com.example.kit.data.RepositoryInterface

class BaseApplication: Application() {
    val contactRepository: RepositoryInterface
        get() = ServiceLocator.provideContactRepository(this)
}