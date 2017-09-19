package ru.xmn.torrentreminder.features.torrent

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert
import org.junit.Before
import org.junit.Test

class TorrentSearchUseCaseTest {
    @Before
    fun setUp() {
        RxJavaPlugins.setInitIoSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun searchTorrents() {
        val searchString = "no matter"
        val searcher = TorrentSearchUseCase(getTorrentSearcherWithUpdates(), getTorrentSearchLastResultRepository())
        val subscriberSearches: TestSubscriber<List<TorrentSearch>> = TestSubscriber()
        searcher.subscribeAllSearches().subscribe(subscriberSearches)
        val subscriberSearch: TestSubscriber<TorrentSearch> = TestSubscriber()
        searcher.subscribeSearch(searchString).subscribe(subscriberSearch)

        //первый запрос - два новых итема
        searcher.search(searchString)

        //второй запрос - новые стали старыми, один новый
        searcher.search(searchString)

        //третий запрос - все стали старыми
        searcher.search(searchString)

        val expected1 = TorrentSearch(searchString, firstQuery.map { TorrentItem(it, true) })
        val expected2 = TorrentSearch(searchString, listOf(
                TorrentItem(TorrentData("name1", "url"), false),
                TorrentItem(TorrentData("name2", "url"), false),
                TorrentItem(TorrentData("name3", "url"), true)
        ))
        val expected3 = TorrentSearch(searchString, updatedQuery.map { TorrentItem(it, false) })

        listOf(expected1, expected2, expected3).zip(subscriberSearches.events[0]).forEach { Assert.assertEquals(listOf(it.first), it.second) }
        listOf(expected1, expected2, expected3).zip(subscriberSearch.events[0]).forEach { Assert.assertEquals(it.first, it.second) }
    }

    private fun getTorrentSearchLastResultRepository(): TorrentSearchRepository {
        return object : TorrentSearchRepository {
            val resultsSubject: PublishSubject<List<TorrentSearch>> = PublishSubject.create<List<TorrentSearch>>()
            val resultSubject: PublishSubject<TorrentSearch> = PublishSubject.create<TorrentSearch>()
            var result: TorrentSearch = TorrentSearch("", emptyList<TorrentItem>())

            override fun subscribeSearch(query: String): Flowable<TorrentSearch> {
                return resultSubject.toFlowable(BackpressureStrategy.LATEST)
            }

            override fun delete(result: TorrentSearch) {

            }

            override fun insertOrUpdate(result: TorrentSearch) {
                resultsSubject.onNext(listOf(result))
                resultSubject.onNext(result)
                this.result = result
            }

            override fun subscribeAllSearches(): Flowable<List<TorrentSearch>> {
                return resultsSubject.toFlowable(BackpressureStrategy.LATEST)
            }

            override fun get(query: String): TorrentSearch {
                return result
            }

        }
    }

    private val firstQuery: List<TorrentData> = listOf(
            TorrentData("name1", "url"),
            TorrentData("name2", "url")
    )

    private val updatedQuery = listOf(
            TorrentData("name1", "url"),
            TorrentData("name2", "url"),
            TorrentData("name3", "url")
    )

    private fun getTorrentSearcherWithUpdates(): TorrentSearcher {
        return object : TorrentSearcher {
            var isFirstSearch = true
            override fun searchTorrents(query: String): List<TorrentData> {
                return when {
                    isFirstSearch -> {
                        isFirstSearch = false
                        firstQuery
                    }
                    else -> updatedQuery
                }
            }
        }
    }
}