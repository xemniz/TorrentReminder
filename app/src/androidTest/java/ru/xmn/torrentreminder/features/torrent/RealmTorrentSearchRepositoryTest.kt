package ru.xmn.torrentreminder.features.torrent

import android.support.test.InstrumentationRegistry
import android.support.test.annotation.UiThreadTest
import android.support.test.runner.AndroidJUnit4
import io.reactivex.subscribers.TestSubscriber
import io.realm.Realm
import io.realm.RealmConfiguration
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.xmn.torrentreminder.features.torrent.dataaccess.RealmTorrentSearch
import ru.xmn.torrentreminder.features.torrent.dataaccess.RealmTorrentSearchRepository
import ru.xmn.torrentreminder.features.torrent.dataaccess.fromRealm
import ru.xmn.torrentreminder.features.torrent.domain.TorrentData
import ru.xmn.torrentreminder.features.torrent.domain.TorrentItem
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch

@RunWith(AndroidJUnit4::class)
class RealmTorrentSearchRepositoryTest {
    val searchRepository = RealmTorrentSearchRepository()
    lateinit var realm: Realm

    @Before
    fun setUp() {
        val testConfig = RealmConfiguration.Builder().name("test-realm").deleteRealmIfMigrationNeeded().build()
        Realm.init(InstrumentationRegistry.getTargetContext())
        Realm.setDefaultConfiguration(testConfig)
        realm = Realm.getDefaultInstance()

        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @After
    fun tearDown() {
        realm.close()
    }


    @UiThreadTest
    @Test
    fun insert() {
        val realm = Realm.getDefaultInstance()
        searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        Thread.sleep(1000)
        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList.size)
        assertEquals(TorrentSearch("searchQuery", "searchQuery", listOf(
                TorrentItem(TorrentData("1", "1"), false),
                TorrentItem(TorrentData("2", "2"), false)
        )).lastSearchedItems, realmList[0].fromRealm().lastSearchedItems)


        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }


    @UiThreadTest
    @Test
    fun delete() {
        val realm = Realm.getDefaultInstance()
        val id = insertOneSearch()

        searchRepository.delete(id)

        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(0, realmList.size)


        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @UiThreadTest
    @Test
    fun checkAllAsViewed() {
        val realm = Realm.getDefaultInstance()
        val id = insertOneSearch()

        searchRepository.checkAllItemsInSearchAsViewed(id)

        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList.size)
        assertEquals(
                TorrentSearch(id, "searchQuery", listOf(
                        TorrentItem(TorrentData("1", "1"), true),
                        TorrentItem(TorrentData("2", "2"), true)
                )), realmList[0].fromRealm())


        searchRepository.update(id, "searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))

        val realmList2 = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList2.size)
        assertEquals(
                TorrentSearch(id, "searchQuery", listOf(
                        TorrentItem(TorrentData("1", "1"), true),
                        TorrentItem(TorrentData("2", "2"), true),
                        TorrentItem(TorrentData("3", "3"), false)
                )), realmList[0].fromRealm())

        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @UiThreadTest
    @Test
    fun testUpdateAll() {
        //когда добавили один итем, получили событие
        val subscribeAll = TestSubscriber.create<List<TorrentSearch>>()
        searchRepository.subscribeAllSearches().subscribe(subscribeAll)
        searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        subscribeAll.awaitCount(2)
        val id = (subscribeAll.events[0][1] as List<TorrentSearch>)[0].id
        assertEquals(1, searchRepository.searchAllChangeListeners.size)

        //когда добавили второй итем и вторую подписку, получили событие и листенера стало два
        val subscribeAll2 = TestSubscriber.create<List<TorrentSearch>>()
        searchRepository.subscribeAllSearches().subscribe(subscribeAll2)
        searchRepository.insert("searchQuery2", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        subscribeAll2.awaitCount(2)
        val id2 = (subscribeAll2.events[0][1] as List<TorrentSearch>)[1].id
        assertEquals(2, searchRepository.searchAllChangeListeners.size)

        subscribeAll.awaitCount(3)
        assertEquals("searchQuery2",(subscribeAll.events[0][2] as List<TorrentSearch>)[1].searchQuery )

        //когда отписались от одной из подписок, остался один листенер
        subscribeAll2.dispose()
        assertEquals(1, searchRepository.searchAllChangeListeners.size)

        //удалили один из итемов, листенер жив
        searchRepository.delete(id)

        subscribeAll.awaitCount(4)
        assertEquals("searchQuery2",(subscribeAll.events[0][3] as List<TorrentSearch>)[0].searchQuery )
    }

    private fun insertOneSearch(searchQuery: String = "searchQuery", dataList: List<TorrentData> = listOf(TorrentData("1", "1"), TorrentData("2", "2"))): String {
        val subscribeAll = TestSubscriber.create<List<TorrentSearch>>()
        searchRepository.subscribeAllSearches().subscribe(subscribeAll)
        searchRepository.insert(searchQuery, dataList)
        subscribeAll.awaitCount(1)
        val id = (subscribeAll.events[0][1] as List<TorrentSearch>)[0].id
        return id
    }
}
