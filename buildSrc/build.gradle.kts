import org.carrat.webidl.build.fetchgrammar.gradle.FetchWebIdlGrammarExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    id("java-gradle-plugin")
    antlr
    id("fetchWebIdlGrammar")
}

repositories {
    jcenter()
    mavenCentral()
    maven("https://carrat.jfrog.io/artifactory/carrat-dev/")
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

val dukatVersion = "0.5.8-rc.4"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.sun.xsom:xsom:20140925")

    implementation(kotlin("serialization"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.1.0-RC")
    implementation("com.charleskorn.kaml:kaml:0.26.0")

    implementation("io.ktor:ktor-client-cio:1.3.1")

    implementation("org.jsoup:jsoup:1.13.1")
    implementation(gradleApi())
    implementation(kotlin("gradle-plugin", version = "1.5.0"))

    antlr("org.antlr:antlr4:4.5")

    implementation("com.squareup:kotlinpoet:1.8.0")
    implementation("com.google.guava:guava:30.1-jre")
    implementation("org.carrat.thirdparty.dukat:idl-reference-resolver:$dukatVersion")
//    implementation("org.carrat.thirdparty.dukat:idl-parser:0.5.8-rc.4")
    implementation("org.carrat.thirdparty.dukat:idl-declarations:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:idl-lowerings:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:idl-models:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:model-lowerings-common:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:model-lowerings:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:panic:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:stdlib:$dukatVersion")

    implementation("org.carrat.thirdparty.dukat:ast-common:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:ast-model:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:compiler:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:translator:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:translator-string:$dukatVersion")
    implementation("org.carrat.thirdparty.dukat:logging:$dukatVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

tasks.withType<AntlrTask> {
    arguments.add("-visitor")
}
