package ru.xmn.torrentreminder.application.di

import dagger.Component
import ru.xmn.torrentreminder.screens.torrentitem.TorrentItemsComponent
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class
))
interface ApplicationComponent {
    fun torrentItemsComponent(): TorrentItemsComponent.Builder
}

