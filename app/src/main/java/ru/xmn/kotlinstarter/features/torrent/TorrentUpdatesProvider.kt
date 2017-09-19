package ru.xmn.kotlinstarter.features.torrent

/**
 * Created by USER on 19.09.2017.
 */
class TorrentSearcherWithUpdates(val torrentSearcher: TorrentSearcher, val lastResultRepository: TorrentSearchLastResultRepository){
    fun searchTorrents(query: String): SearchWithUpdatesResult {
        val lastQueriedItems = torrentSearcher.searchTorrents(query)
        val lastSavedItems = lastResultRepository.getLastResult(query)
        lastResultRepository.setLastResult(query, lastQueriedItems)

        val newItems = lastQueriedItems - lastSavedItems
        if (newItems != emptyList<TorrentItem>())
            return SearchWithUpdatesResult.HasNewItems(lastQueriedItems.map { TorrentItemWithUpdates(it, newItems.contains(it)) })
        else
            return SearchWithUpdatesResult.HasNotNewItems(lastQueriedItems)
    }
}

interface TorrentSearchLastResultRepository {
    fun setLastResult(query: String, items: List<TorrentItem>)
    fun getLastResult(query: String): List<TorrentItem>
}

sealed class SearchWithUpdatesResult {
    data class HasNewItems(val items: List<TorrentItemWithUpdates>): SearchWithUpdatesResult()
    data class HasNotNewItems(val items: List<TorrentItem>): SearchWithUpdatesResult()
}

data class TorrentItemWithUpdates(val item: TorrentItem, val isUpdate: Boolean)

data class TorrentSearchQuery(val query: String)