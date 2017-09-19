package ru.xmn.torrentreminder.features.torrent

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subscribers.TestSubscriber
import junit.framework.Assert
import org.junit.Before
import org.junit.Test
import rx.schedulers.Schedulers.immediate
import rx.plugins.RxJavaSchedulersHook



/**
 * Created by USER on 19.09.2017.
 */
class TorrentSearchUseCaseTest {
    @Before
    fun setUp() {
        RxJavaPlugins.setInitIoSchedulerHandler {
            Schedulers.trampoline()
        }
    }

    @Test
    fun searchTorrents() {
        val searcher = TorrentSearchUseCase(getTorrentSearcherWithUpdates(), getTorrentSearchLastResultRepository())
        val subscriber: TestSubscriber<List<TorrentSearch>> = TestSubscriber()
        searcher.getAllSearches().subscribe(subscriber)

        //первый запрос - два новых итема
        searcher.insertOrUpdateSearch("no matter")

        //второй запрос - новые стали старыми, один новый
        searcher.insertOrUpdateSearch("no matter")

        //третий запрос - все стали старыми
        searcher.insertOrUpdateSearch("no matter")

        val expected1 = TorrentSearch("no matter", firstQuery.map { TorrentItem(it, true) })
        val expected2 = TorrentSearch("no matter", listOf(
                TorrentItem(TorrentData("name1", "url"), false),
                TorrentItem(TorrentData("name2", "url"), false),
                TorrentItem(TorrentData("name3", "url"), true)
        ))
        val expected3 = TorrentSearch("no matter", updatedQuery.map { TorrentItem(it, false) })

        listOf(expected1, expected2, expected3).zip(subscriber.events[0]).forEach { Assert.assertEquals(listOf(it.first), it.second) }
    }

    private fun getTorrentSearchLastResultRepository(): TorrentSearchRepository {
        return object : TorrentSearchRepository {
            override fun delete(result: TorrentSearch) {

            }

            val resultSubject: PublishSubject<List<TorrentSearch>> = PublishSubject.create<List<TorrentSearch>>()
            var result: TorrentSearch = TorrentSearch("", emptyList<TorrentItem>())

            override fun insertOrUpdate(result: TorrentSearch) {
                resultSubject.onNext(listOf(result))
                this.result = result
            }

            override fun getAll(): Flowable<List<TorrentSearch>> {
                return resultSubject.toFlowable(BackpressureStrategy.LATEST)
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