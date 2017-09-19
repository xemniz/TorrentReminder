package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable
import io.reactivex.Single

class TorrentSearchUseCase (val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    fun insertOrUpdateSearch(searchString: String): TorrentSearch {
        val lastQueriedItems = torrentSearcher.searchTorrents(searchString)
        val lastSavedItems = torrentSearchRepository.get(searchString).lastSearchedItems.map { it.item }

        val searchResult = TorrentSearch(searchString, lastQueriedItems.map { TorrentItem(it, !lastSavedItems.contains(it)) })

        torrentSearchRepository.insertOrUpdate(searchResult)

        return searchResult
    }

    fun getAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.getAll()
    }
}

interface TorrentSearchRepository {
    fun insertOrUpdate(result: TorrentSearch)
    fun delete(result: TorrentSearch)
    fun get(query: String): TorrentSearch
    fun getAll(): Flowable<List<TorrentSearch>>
}

data class TorrentItem(val item: TorrentData, val isUpdate: Boolean)

data class TorrentSearch(val query: String, val lastSearchedItems: List<TorrentItem>)