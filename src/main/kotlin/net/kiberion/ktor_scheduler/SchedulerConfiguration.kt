package net.kiberion.ktor_scheduler

import org.jobrunr.storage.AbstractStorageProvider
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SchedulerConfiguration private constructor() {
    lateinit var storageProvider: AbstractStorageProvider
    var threads: Int = 10
    var pollInterval: Duration = 15.seconds

    companion object {
        fun create(): SchedulerConfiguration {
            return SchedulerConfiguration()
        }
    }
}
