package ru.xmn.torrentreminder.screens.torrentsearch

import ru.xmn.torrentreminder.features.torrent.TorrentSearch

sealed class TorrentSearchState {
    object Loading : TorrentSearchState()

    class Success(val items: List<TorrentSearch>) : TorrentSearchState()

    class UpdateComplete(val complete: Boolean ) : TorrentSearchState()

    class Error(val error: Throwable) : TorrentSearchState(){
        init {
            error.printStackTrace()
        }
    }
}