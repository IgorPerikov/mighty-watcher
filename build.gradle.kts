import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    val kotlinVersion = "1.3.41"
    repositories {
        maven("https://plugins.gradle.org/m2/")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

apply(plugin = "org.jetbrains.kotlin.jvm")

plugins {
    application
}

application {
    mainClassName = "com.github.igorperikov.mightywatcher.Launcher"
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

val kotlinCoroutinesVersion = "1.3.0-M2"
val okHttpVersion = "3.14.2"
val jacksonVersion = "2.9.9"
val slf4jVersion = "1.7.26"
val logbackVersion = "1.2.3"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", kotlinCoroutinesVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core-common", kotlinCoroutinesVersion)

    implementation("com.squareup.okhttp3", "okhttp", okHttpVersion)

    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", jacksonVersion)
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", jacksonVersion)
    implementation("com.fasterxml.jackson.dataformat", "jackson-dataformat-yaml", jacksonVersion)

    implementation("org.slf4j", "slf4j-api", slf4jVersion)
    implementation("ch.qos.logback", "logback-core", logbackVersion)
    implementation("ch.qos.logback", "logback-classic", logbackVersion)
}
