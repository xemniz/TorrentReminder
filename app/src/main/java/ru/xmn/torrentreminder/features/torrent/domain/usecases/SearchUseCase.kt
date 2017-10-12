package ru.xmn.torrentreminder.features.torrent.domain.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import ru.xmn.torrentreminder.features.torrent.domain.*
import javax.inject.Inject

class SearchUseCase
@Inject
constructor(val torrentSearcher: TorrentSearcher, val torrentSearchRepository: TorrentSearchRepository) {
    val searchResult: PublishProcessor<SearchResult> = PublishProcessor.create()
    val searchIsSaved: PublishProcessor<Boolean> = PublishProcessor.create()

    fun addNewItem(searchQuery: String) {
        Single.fromCallable { torrentSearchRepository.insert(searchQuery, emptyList()) }
                .flatMapCompletable { id ->
                    Single.fromCallable { torrentSearcher.searchTorrents(searchQuery) }
                            .flatMapCompletable {
                                Completable.fromAction {
                                    torrentSearchRepository.update(id, searchQuery, it)
                                }
                            }
                }
                .onErrorComplete()
                .subscribe()
    }

    fun bindQueryFlowable(searchQueryFlowable: Flowable<String>) {
        Flowable.combineLatest<String, List<TorrentSearch>, Pair<String, List<TorrentSearch>>>(
                searchQueryFlowable,
                torrentSearchRepository.subscribeAllSearches(),
                BiFunction { query: String, searches: List<TorrentSearch> ->
                    Pair(query, searches)
                })
                .map {
                    val (query, searches) = it
                    searches.any { it.searchQuery == query }
                }
                .subscribe(searchIsSaved)


        searchQueryFlowable
                .flatMap { query: String ->
                    torrentSearchRepository.subscribeAllSearches().take(1).map { Pair(query, it) }
                }
                .flatMap {
                    val (query, searches) = it
                    val search = searches.firstOrNull { it.searchQuery == query }
                    if (search == null)
                        return@flatMap search(query)
                                .map { it.map { TorrentItem(it, false) } }
                                .map { SearchResult.NewSearch(it) }
                    else
                        return@flatMap subscribeSearchAndUpdate(search)
                                .map { SearchResult.SavedSearch(it) }
                }
                .subscribe(searchResult)
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
}

sealed class SearchResult {
    class NewSearch(val list: List<TorrentItem>) : SearchResult()
    class SavedSearch(val search: TorrentSearch) : SearchResult()
}