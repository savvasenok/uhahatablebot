plugins {
    kotlin("jvm") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "xyz.savvamirzoyan"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.inmo:tgbotapi:18.2.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "MainKt"
    }
}
