import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
    application
    id("com.github.johnrengelman.shadow") version "5.1.0"
    jacoco
}

application {
    mainClassName = "com.github.igorperikov.mightywatcher.Launcher"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
}

tasks {
    named<ShadowJar>("shadowJar") {
        archiveBaseName.set("mighty-watcher")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = false
    }
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

val kotlinCoroutinesVersion = "1.3.1"
val okHttpVersion = "3.14.2"
val jacksonVersion = "2.9.9"
val slf4jVersion = "1.7.28"
val logbackVersion = "1.2.3"
val junit5Version = "5.5.2"
val hamkrestVersion = "1.7.0.0"
val mockitoKotlin2Version = "2.2.0"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", kotlinCoroutinesVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core-common", kotlinCoroutinesVersion)

    implementation("com.squareup.okhttp3", "okhttp", okHttpVersion)

    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)

    implementation("org.slf4j", "slf4j-api", slf4jVersion)
    implementation("ch.qos.logback", "logback-core", logbackVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-html-jvm", "0.6.10")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", junit5Version)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit5Version)

    testImplementation("com.natpryce", "hamkrest", hamkrestVersion)
    testImplementation("com.nhaarman.mockitokotlin2", "mockito-kotlin", mockitoKotlin2Version)
}
