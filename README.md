# ktor-scheduler

Cluster-friendly task-scheduler for ktor

Uses [jobrunr](https://github.com/jobrunr/jobrunr) under-the-hood for managing jobs.
All recurring jobs and enqueued tasks are stored in specified storage (either durable or transient).

## Installing

Gradle:
```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.jombi:ktor3-scheduler:0.0.1")
    implementation("org.jobrunr:jobrunr:7.4.1")
}
```

## Example usage

### Recurring job

```kotlin
fun Application.configureJobs() {
    install(Scheduler) {
        storageProvider = H2StorageProvider(initH2Database())
        threads = 5
        pollInterval = 30.seconds
    }

    schedule {
        recurringJob("someUniqueId", Cron.minutely()) {
            jobPayload() // note that there are severe limitations on what can be included in lambda itself, so in most cases you should extract job logic into a separate function
        }
    }

    fun initH2Database(): DataSource {
        val config = HikariConfig()
        config.setJdbcUrl("jdbc:h2:mem:test_mem")
        config.setUsername("sa")
        config.setPassword("")
        return HikariDataSource(config)
    }

    fun jobPayload() {
        // Job logic here
    }
}
```

### One-off task

```kotlin
val scheduler: Scheduler = application.attributes[Scheduler.SchedulerKey]

scheduler.scheduleEnqueuedTask { taskPayload(1) }
scheduler.scheduleEnqueuedTask { taskPayload(3) }
scheduler.scheduleEnqueuedTask { taskPayload(10) }

fun taskPayload(something: Int) {
    // Task logic here...
}

```
