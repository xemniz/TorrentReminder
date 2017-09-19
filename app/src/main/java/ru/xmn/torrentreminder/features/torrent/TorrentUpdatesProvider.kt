package ru.xmn.torrentreminder.features.torrent

import io.reactivex.Flowable

class TorrentSearchUseCase (val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    fun search(searchString: String): TorrentSearch {
        val lastQueriedItems = torrentSearcher.searchTorrents(searchString)
        val lastSavedItems = torrentSearchRepository.get(searchString).lastSearchedItems.map { it.item }

        val searchResult = TorrentSearch(searchString, lastQueriedItems.map { TorrentItem(it, !lastSavedItems.contains(it)) })

        torrentSearchRepository.insertOrUpdate(searchResult)

        return searchResult
    }

    fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
        return torrentSearchRepository.subscribeAllSearches()
    }

    fun subscribeSearch(query: String): Flowable<TorrentSearch> {
        return torrentSearchRepository.subscribeSearch(query)
    }
}

interface TorrentSearchRepository {
    fun insertOrUpdate(result: TorrentSearch)
    fun delete(result: TorrentSearch)
    fun get(query: String): TorrentSearch
    fun subscribeSearch(query: String): Flowable<TorrentSearch>
    fun subscribeAllSearches(): Flowable<List<TorrentSearch>>
}

data class TorrentItem(val item: TorrentData, val isUpdate: Boolean)

data class TorrentSearch(val query: String, val lastSearchedItems: List<TorrentItem>){
    val hasUpdates: Boolean
    get() = lastSearchedItems.firstOrNull { it.isUpdate }?.isUpdate?:false
}