package ru.xmn.torrentreminder.features.torrent.di

import dagger.Subcomponent
import ru.xmn.torrentreminder.application.di.scopes.ActivityScope
import ru.xmn.torrentreminder.screens.torrentlist.TorrentListViewModule
import ru.xmn.torrentreminder.screens.torrentsearch.TorrentSearchViewModel

@ActivityScope
@Subcomponent(modules = arrayOf(TorrentModule::class))
interface TorrentComponent {
    fun inject(listViewModule: TorrentListViewModule)
    fun inject(listViewModule: TorrentSearchViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): TorrentComponent

        fun provideModule(r: TorrentModule): Builder

    }
}