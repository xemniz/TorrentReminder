package ru.xmn.torrentreminder.application.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.RealmTorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.TorrentSearchRepository
import ru.xmn.torrentreminder.screens.torrentsearch.TorrentSearchComponent
import javax.inject.Singleton

@Module(subcomponents = arrayOf(TorrentSearchComponent::class))
class ApplicationModule(private val app: App) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = app

    @Provides
    fun provideTorrentSearchRepository(): TorrentSearchRepository = RealmTorrentSearchRepository()
}