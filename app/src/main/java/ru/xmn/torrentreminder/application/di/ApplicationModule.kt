package ru.xmn.torrentreminder.application.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.dataaccess.RealmTorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.di.TorrentComponent
import javax.inject.Singleton

@Module(subcomponents = arrayOf(TorrentComponent::class))
class ApplicationModule(private val app: App) {
    @Provides
    @Singleton
    fun provideApplicationContext(): Context = app

    @Provides
    fun provideTorrentSearchRepository(): TorrentSearchRepository = RealmTorrentSearchRepository()
}