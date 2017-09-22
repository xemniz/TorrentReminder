package ru.xmn.torrentreminder.application.di

import dagger.Component
import ru.xmn.torrentreminder.screens.torrentsearch.TorrentSearchComponent
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class
))
interface ApplicationComponent {
    fun torrentItemsComponent(): TorrentSearchComponent.Builder
}

