package ru.xmn.torrentreminder.application.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.screens.AbstractComponent
import javax.inject.Singleton

@Module(subcomponents = arrayOf(AbstractComponent::class))
class ApplicationModule(private val app: App) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = app
}