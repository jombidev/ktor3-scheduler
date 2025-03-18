package dev.jombi.ktor.plugins.scheduler

import io.ktor.server.application.*
import io.ktor.server.testing.*
import kotlinx.coroutines.*
import org.awaitility.Awaitility.await
import org.awaitility.kotlin.atMost
import org.jobrunr.storage.InMemoryStorageProvider
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

private val atomicCounter = AtomicInteger()

class SchedulerTest {
    companion object {
        const val EVERY_5S_CRON = "*/5 * * * * *"
    }

    private fun Application.testModule() {
        install(Scheduler) {
            storageProvider = InMemoryStorageProvider()
            pollInterval = 1.seconds
            threads = 3
        }
    }

    private fun taskIncrementOnlyOnce(addedValue: Int) {
        val oldValue = atomicCounter.getAndAdd(addedValue)
        if (oldValue > 0) {
            throw IllegalStateException("Exceeded amount of increments")
        }
    }

    private fun taskIncrement(addedValue: Int) {
        atomicCounter.getAndAdd(addedValue)
    }

    @BeforeTest
    fun `set the atomic counter to zero`() {
        atomicCounter.set(0)
    }

    @Test
    fun `should execute recurring job`() {
        testApplication {
            assertEquals(0, atomicCounter.get())

            application {
                testModule()
                schedule {
                    recurringJob("incCounter", EVERY_5S_CRON) { // every seconds
                        taskIncrementOnlyOnce(1)
                    }
                }
            }

            startApplication()

            await()
                .atMost(6.seconds)
                .until {
                    atomicCounter.get() == 1
                }
        }
    }

    @Test
    fun `should override existing recurring job when id match`(): Unit = testApplication {
        assertEquals(0, atomicCounter.get())

        application {
            testModule()
            schedule {
                recurringJob("incCounter", EVERY_5S_CRON) {
                    taskIncrementOnlyOnce(1)
                }
            }
            schedule {
                recurringJob("incCounter", EVERY_5S_CRON) {
                    taskIncrementOnlyOnce(2)
                }
            }
        }

        startApplication()

        await()
            .atMost(6.seconds)
            .until {
                atomicCounter.get() == 2
            }
    }

    @Test
    fun `should enqueue multiple tasks`(): Unit = testApplication {
        assertEquals(0, atomicCounter.get())

        application {
            testModule()

            val scheduler = attributes[Scheduler.SchedulerKey]

            scheduler.scheduleEnqueuedTask { taskIncrement(1) }
            scheduler.scheduleEnqueuedTask { taskIncrement(3) }
            scheduler.scheduleEnqueuedTask { taskIncrement(10) }
        }

        startApplication()

        await()
            .atMost(6.seconds)
            .until {
                atomicCounter.get() == 14
            }
    }

    @Test
    fun `should stop execution after application shutdown`() = runBlocking {
        assertEquals(0, atomicCounter.get())

        testApplication {
            application {
                testModule()
                schedule {
                    recurringJob("incCounter", EVERY_5S_CRON) {
                        taskIncrement(1)
                    }
                }
            }
        }

        val oldValue = atomicCounter.get()
        delay(6.seconds)
        assertEquals(oldValue, atomicCounter.get())
    }
}
