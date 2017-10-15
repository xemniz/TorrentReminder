package ru.xmn.torrentreminder.features.torrent

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.firebase.jobdispatcher.*
import io.reactivex.functions.Consumer
import org.jetbrains.anko.toast
import ru.xmn.common.extensions.log
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.domain.TorrentSearch
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SavedSearchServiceUseCase
import ru.xmn.torrentreminder.screens.torrentsearch.fragments.savedsearches.SavedSearchesFragment
import javax.inject.Inject

class ScheduledJobService : JobService() {
    @Inject
    lateinit var savedSearchServiceUseCase: SavedSearchServiceUseCase

    override fun onCreate() {
        super.onCreate()
        App.component.torrentItemsComponent().build().inject(this)
    }

    override fun onStartJob(job: JobParameters): Boolean {
        applicationContext.toast("start job")
        savedSearchServiceUseCase.updateAllItems().subscribe(Consumer {
            //            if (it.isNotEmpty())
            sendNotification(applicationContext, it)
            jobFinished(job, true)
        })
        return true
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return true
    }

    fun sendNotification(context: Context, it: ArrayList<TorrentSearch>) {

        val intent = Intent(context, SavedSearchesFragment::class.java)
        val pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val builder = NotificationCompat.Builder(context)

        builder.setContentIntent(pIntent)
                .setSmallIcon(android.R.drawable.ic_media_ff)
                .setContentTitle("Обновлено ${it.size} торрентов")
                .setContentText("")
                .setDefaults(Notification.DEFAULT_VIBRATE)

        val notification = NotificationCompat
                .BigTextStyle(builder)
                .bigText(updatedItems(it))
                .build()
        NotificationManagerCompat.from(context).notify(0, notification)
    }

    private fun updatedItems(list: ArrayList<TorrentSearch>): String {
        var s = ""
        list.forEach { s += it.searchQuery + " +" + it.lastSearchedItems.filter { !it.isViewed }.size + "\n" }
        return s
    }

    companion object {
        private const val DAY_IN_MILLIS = 60 * 60 * 24
        private const val DAILY_JOB_TAG = "UpdateAllTorrentsEveryDay"

        fun scheduleJob(context: Context) {
            log("scheduling job")
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            val job = createDailyJob(dispatcher)
            dispatcher.mustSchedule(job)
        }

        private fun createDailyJob(dispatcher: FirebaseJobDispatcher): Job {
            return dispatcher.newJobBuilder()
                    .setLifetime(Lifetime.FOREVER)
                    .setService(ScheduledJobService::class.java)
                    .setTag(DAILY_JOB_TAG)
                    .setReplaceCurrent(true)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(20, 100))
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build()
        }
    }
}
