package ru.xmn.torrentreminder.features.torrent

import android.os.Handler
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

    @Test
    fun insertOrUpdate() {
        searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        Thread.sleep(1000)
        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList.size)
        assertEquals(TorrentSearch("searchQuery","searchQuery", listOf(
                TorrentItem(TorrentData("1", "1"), false),
                TorrentItem(TorrentData("2", "2"), false)
        )).lastSearchedItems, realmList[0].fromRealm().lastSearchedItems)


        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @Test
    fun delete() {
        searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        searchRepository.delete("searchQuery")

        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(0, realmList.size)


        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }
    }

    @UiThreadTest
    @Test
    fun checkAllAsViewed() {
        val realm = Realm.getDefaultInstance()
        val subscriber = TestSubscriber.create<List<TorrentSearch>>()
        searchRepository.subscribeAllSearches().subscribe(subscriber)
        searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))
        subscriber.awaitCount(1)
        val id = (subscriber.events[0][1] as List<TorrentSearch>)[0].id
        searchRepository.checkAllItemsInSearchAsViewed(id)

        val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList.size)
        assertEquals(
                TorrentSearch(id,"searchQuery", listOf(
                        TorrentItem(TorrentData("1", "1"), true),
                        TorrentItem(TorrentData("2", "2"), true)
                )).lastSearchedItems, realmList[0].fromRealm().lastSearchedItems)


        searchRepository.update(id, "searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))

        val realmList2 = realm.where(RealmTorrentSearch::class.java).findAll()
        assertEquals(1, realmList2.size)
        assertEquals(
                TorrentSearch("searchQuery","searchQuery", listOf(
                        TorrentItem(TorrentData("1", "1"), true),
                        TorrentItem(TorrentData("2", "2"), true),
                        TorrentItem(TorrentData("3", "3"), false)
                )).lastSearchedItems, realmList[0].fromRealm().lastSearchedItems)

        realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

    }

    @UiThreadTest
    @Test
    fun testSynchronize() {
        val realm = Realm.getDefaultInstance()

        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}

        Handler().postDelayed({
            val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
            assertEquals(1, realmList.size)
            assertEquals(
                    TorrentSearch("searchQuery","searchQuery", listOf(
                            TorrentItem(TorrentData("1", "1"), true),
                            TorrentItem(TorrentData("2", "2"), true),
                            TorrentItem(TorrentData("3", "3"), false)
                    )).lastSearchedItems, realmList[0].fromRealm().lastSearchedItems)

            realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

        }, 1000)
        Thread.sleep(1100)
    }

    @UiThreadTest
    @Test
    fun testSynchronizeStress() {
        val realm = Realm.getDefaultInstance()

        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}

        Handler().postDelayed({
            val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
            assertEquals(1, realmList.size)
            assertEquals(
                    TorrentSearch("searchQuery","searchQuery", listOf(
                            TorrentItem(TorrentData("1", "1"), true),
                            TorrentItem(TorrentData("2", "2"), true),
                            TorrentItem(TorrentData("3", "3"), true)
                    )).lastSearchedItems, realmList[0].fromRealm().lastSearchedItems)

            realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

        }, 1000)
        Thread.sleep(1100)
    }

    @UiThreadTest
    @Test
    fun testSynchronizeDelete() {
        val realm = Realm.getDefaultInstance()

        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2")))}
        Handler().post{searchRepository.checkAllItemsInSearchAsViewed("searchQuery")}
        Handler().post{searchRepository.insert("searchQuery", listOf(TorrentData("1", "1"), TorrentData("2", "2"), TorrentData("3", "3")))}
        Handler().post{searchRepository.delete("searchQuery")}

        Handler().postDelayed({
            val realmList = realm.where(RealmTorrentSearch::class.java).findAll()
            assertEquals(0, realmList.size)

            realm.executeTransaction { it.where(RealmTorrentSearch::class.java).findAll().deleteAllFromRealm() }

        }, 1000)
        Thread.sleep(1100)
    }
}