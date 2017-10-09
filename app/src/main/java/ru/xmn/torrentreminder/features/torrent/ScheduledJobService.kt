package ru.xmn.torrentreminder.features.torrent

import android.content.Context
import com.firebase.jobdispatcher.*
import io.reactivex.functions.Consumer
import ru.xmn.torrentreminder.application.App
import ru.xmn.torrentreminder.features.torrent.domain.usecases.SavedSearchServiceUseCase
import javax.inject.Inject


class ScheduledJobService : JobService() {
    @Inject
    lateinit var savedSearchServiceUseCase: SavedSearchServiceUseCase

    override fun onCreate() {
        super.onCreate()
        App.component.torrentItemsComponent().build().inject(this)
        println(savedSearchServiceUseCase)
    }

    override fun onStartJob(job: JobParameters?): Boolean {
        savedSearchServiceUseCase.updateAllItems().subscribe(Consumer { println(it) })
        return true
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        return false
    }

    companion object {
        private val DAY_IN_MILLIS = 60 * 60 * 24

        fun scheduleJob(context: Context) {
            val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(context))
            val job = createJob(dispatcher)
            dispatcher.mustSchedule(job)
        }

        private fun createJob(dispatcher: FirebaseJobDispatcher): Job {

            return dispatcher.newJobBuilder()
                    .setLifetime(Lifetime.FOREVER)
                    .setService(ScheduledJobService::class.java)
                    .setTag("UpdateAllTorrentsItemEveryDay")
                    .setReplaceCurrent(true)
                    .setRecurring(true)
                    .setTrigger(Trigger.executionWindow(5, DAY_IN_MILLIS))
                    .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                    .setConstraints(Constraint.ON_ANY_NETWORK)
                    .build()
        }
    }
}
