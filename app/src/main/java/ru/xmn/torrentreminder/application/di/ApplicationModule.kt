package ru.xmn.torrentreminder.application.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.screens.torrentitem.TorrentItemsComponent
import javax.inject.Singleton

@Module(subcomponents = arrayOf(TorrentItemsComponent::class))
class ApplicationModule(private val app: App) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = app
}