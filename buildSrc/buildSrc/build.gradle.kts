plugins {
    kotlin("jvm") version "1.4.20"
    id("java-gradle-plugin")
}

repositories {
    jcenter()
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("fetchWebIdlGrammar") {
            id = "fetchWebIdlGrammar"
            implementationClass = "org.carrat.webidl.build.fetchgrammar.gradle.FetchWebIdlGrammarPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.0")
    implementation("com.google.guava:guava:30.0-jre")

    implementation("io.ktor:ktor-client-cio:1.3.1")

    implementation("org.jsoup:jsoup:1.13.1")
    implementation(gradleApi())
}