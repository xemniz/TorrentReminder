package ru.xmn.torrentreminder.screens.torrentsearch

import dagger.Subcomponent
import ru.xmn.torrentreminder.application.di.scopes.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(TorrentSearchModule::class))
interface TorrentSearchComponent {
    fun inject(abstractViewModel: TorrentSearchViewModel)

    @Subcomponent.Builder
    interface Builder {
        fun build(): TorrentSearchComponent
        fun provideModule(r: TorrentSearchModule): Builder

    }
}