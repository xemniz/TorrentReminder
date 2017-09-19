package ru.xmn.torrentreminder.application.di

import dagger.Component
import ru.xmn.torrentreminder.screens.AbstractComponent
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class
))
interface ApplicationComponent {
    fun abstractInteractorComponent(): AbstractComponent.Builder
}

