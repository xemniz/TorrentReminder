package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.*
import javax.inject.Inject

class SearchUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {

    fun addNewItem(searchQuery: String): Flowable<Unit> {
        return Flowable.fromCallable { torrentSearchRepository.insert(searchQuery, emptyList()) }
                .flatMap { id ->
                    Flowable.fromCallable { Pair(id, torrentSearcher.searchTorrents(searchQuery)) }
                }
                .flatMap {
                    Flowable.fromCallable {
                        torrentSearchRepository.update(it.first, searchQuery, it.second)
                    }
                }
    }

    fun isSearchSaved(searchQueryFlowable: Flowable<String>): Flowable<Boolean> {
        return Flowable.combineLatest<String, List<TorrentSearch>, Pair<String, List<TorrentSearch>>>(
                searchQueryFlowable,
                torrentSearchRepository.subscribeAllSearches(),
                BiFunction { query: String, searches: List<TorrentSearch> ->
                    Pair(query, searches)
                })
                .map {
                    val (query, searches) = it
                    searches.any { it.searchQuery == query }
                }

    }

    fun foundedOrSavedItems(searchQuery: String): Flowable<SearchResult> {
        return torrentSearchRepository.subscribeAllSearches()
                .take(1)
                .map { Pair(searchQuery, it) }
                .flatMap {
                    val (query, searches) = it
                    val search = searches.firstOrNull { it.searchQuery == query }
                    if (search == null)
                        return@flatMap search(query)
                                .map { it.map { TorrentItem(it, true) } }
                                .map { SearchResult.NewSearch(it) as SearchResult }
                                .onErrorReturn { SearchResult.Error(it) }
                    else
                        return@flatMap subscribeSearchAndUpdate(search)
                                .map { SearchResult.SavedSearch(it) as SearchResult }
                                .onErrorReturn { SearchResult.Error(it) }
                }
    }

    private fun search(searchQuery: String): Flowable<List<TorrentData>> {
        return Flowable.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                .subscribeOn(Schedulers.io())
    }

    private fun subscribeSearchAndUpdate(search: TorrentSearch): Flowable<TorrentSearch> {
        return torrentSearchRepository
                .subscribeSearch(search.id)
                .doOnNext {
                    Flowable.fromCallable { torrentSearcher.searchTorrents(search.searchQuery) }
                            .map { Pair(search, it) }
                            .subscribeOn(Schedulers.io())
                            .flatMapCompletable {
                                Completable.fromCallable {
                                    torrentSearchRepository.update(it.first.id, it.first.searchQuery, it.second)
                                }
                            }
                }
    }

    fun markAsViewed(id: String) {
        torrentSearchRepository.checkAllItemsInSearchAsViewed(id)
    }
}

sealed class SearchResult {
    class NewSearch(val list: List<TorrentItem>) : SearchResult()
    class SavedSearch(val search: TorrentSearch) : SearchResult()
    class Error(val error: Throwable) : SearchResult()
}