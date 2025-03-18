import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    kotlin("jvm") version libs.versions.kotlin
    id("java-library")
    id("maven-publish")
}

/*publishing {
    publications {
        maven(MavenPublication) {
            groupId = "com.github.kibertoad"
            artifactId = "ktor-scheduler"
            version = "1.0.2"

            from(components["java"])
        }
    }
}*/

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
        apiVersion.set(KotlinVersion.KOTLIN_1_9)
    }
}
