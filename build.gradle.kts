import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version libs.versions.kotlin
    id("com.vanniktech.maven.publish") version "0.31.0-rc2"
}

group = "dev.jombi"
version = "0.0.1"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    coordinates(group.toString(), "ktor3-scheduler", version.toString())
    signAllPublications()

    pom {
        name = "ktor3-scheduler"
        description = " Cluster-friendly task-scheduler for ktor version 3"
        url = "https://github.com/jombidev/ktor3-scheduler"
        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/mit"
            }
        }
        developers {
            developer {
                id = "jombidev"
                name = "Jombi"
                email = "jombi@duck.com"
            }
        }
        scm {
            connection = "scm:git:git://github.com/jombidev/ktor3-scheduler.git"
            developerConnection = "scm:git:ssh://github.com:jombidev/ktor3-scheduler.git"
            url = "https://github.com/jombidev/ktor3-scheduler/tree/master"
        }
    }
}

tasks.javadoc {
    if (JavaVersion.current().isJava9Compatible) {
        (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation(libs.ktor.server.core)
    implementation(libs.jobrunr)

    // TODO: wait for this: https://github.com/jobrunr/jobrunr/pull/1207
    implementation(libs.jackson.core)
    implementation(libs.jackson.kotlin)

    // tests
    testImplementation(libs.commons.codec)
    testImplementation(libs.logback)

    testImplementation(libs.ktor.server.test)
    testImplementation(libs.kotlin.test)

    testImplementation(libs.hikaricp)
    testImplementation(libs.h2)

    testImplementation(libs.awaitility)
    testImplementation(libs.awaitility.kotlin)
}

tasks.test {
    testLogging {
        events("passed", "skipped", "failed")
    }
}

kotlin {
    compilerOptions {
        jvmToolchain(11)
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}
