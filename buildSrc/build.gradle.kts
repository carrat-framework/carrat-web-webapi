import org.carrat.webidl.build.fetchgrammar.gradle.FetchWebIdlGrammarExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.serialization") version "1.4.21"
    id("java-gradle-plugin")
    antlr
    id("fetchWebIdlGrammar")
}

repositories {
    jcenter()
    mavenCentral()
    mavenLocal()
}

gradlePlugin {
    plugins {
        create("fetchWebIdl") {
            id = "fetchWebIdl"
            implementationClass = "org.carrat.webidl.build.fetch.gradle.FetchWebIdlPlugin"
        }
        create("compileWebIdl") {
            id = "compileWebIdl"
            implementationClass = "org.carrat.webidl.build.compile.gradle.CompileWebIdlPlugin"
        }
    }
}

configure<FetchWebIdlGrammarExtension> {
    getOutput().set(projectDir.resolve("src/main/antlr/org/carrat/webidl/build/grammar/WebIdl.g4").absolutePath)
    getPackage().set("org.carrat.webidl.build.grammar")
    getUrl().set("https://heycam.github.io/webidl/")
}

tasks.withType<AntlrTask>().forEach { antlrTask ->
    tasks.withType<KotlinCompile>().forEach {
        it.dependsOn(antlrTask)
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.sun.xsom:xsom:20140925")

    implementation(kotlin("serialization"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0")
    implementation("com.charleskorn.kaml:kaml:0.26.0")

    implementation("io.ktor:ktor-client-cio:1.3.1")

    implementation("org.jsoup:jsoup:1.13.1")
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin", version = "1.4.21"))

    antlr("org.antlr:antlr4:4.5")

    implementation("com.squareup:kotlinpoet:1.8.0-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}
