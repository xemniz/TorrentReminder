package ru.xmn.torrentreminder.screens.torrentsearch

import dagger.Subcomponent
import ru.xmn.torrentreminder.application.di.scopes.ActivityScope
import ru.xmn.torrentreminder.screens.torrentsearch.torrentList.TorrentListViewModule

@ActivityScope
@Subcomponent(modules = arrayOf(TorrentSearchModule::class))
interface TorrentSearchComponent {
    fun inject(abstractViewModel: TorrentSearchViewModel)
    fun injectToListVM(listViewModule: TorrentListViewModule)

    @Subcomponent.Builder
    interface Builder {
        fun build(): TorrentSearchComponent
        fun provideModule(r: TorrentSearchModule): Builder

    }
}