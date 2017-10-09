package ru.xmn.torrentreminder.features.torrent.di

import dagger.Subcomponent
import ru.xmn.torrentreminder.application.di.scopes.ActivityScope
import ru.xmn.torrentreminder.features.torrent.ScheduledJobService
import ru.xmn.torrentreminder.screens.torrentlist.SavedSearchDetailsViewModule
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.TorrentSearchViewModel
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.search.SearchFragmentViewModel

@ActivityScope
@Subcomponent(modules = arrayOf(TorrentModule::class))
interface TorrentComponent {
    fun inject(listViewModule: SavedSearchDetailsViewModule)
    fun inject(listViewModule: TorrentSearchViewModel)
    fun inject(listViewModel: SearchFragmentViewModel)
    fun inject(scheduledJobService: ScheduledJobService)

    @Subcomponent.Builder
    interface Builder {

        fun build(): TorrentComponent

        fun provideModule(r: TorrentModule): Builder

    }
}