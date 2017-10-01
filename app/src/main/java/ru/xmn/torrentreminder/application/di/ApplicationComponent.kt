package ru.xmn.torrentreminder.application.di

import dagger.Component
import ru.xmn.torrentreminder.features.torrent.di.TorrentComponent
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class
))
interface ApplicationComponent {
    fun torrentItemsComponent(): TorrentComponent.Builder
}

