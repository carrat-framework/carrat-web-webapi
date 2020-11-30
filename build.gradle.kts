import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    val kotlinVersion by extra("1.4.20")

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

val kotlinVersion by extra("1.4.20")
val kotlinWrappersVersion by extra("pre.126-kotlin-1.4.10")
val kotlinxSerializationVersion by extra("1.0.0")

plugins {
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("maven-publish")
    id("fetchWebIdl")
    id("compileWebIdl")
    kotlin("multiplatform")
//    id("com.jfrog.bintray") version "1.8.5"
}

group = "org.carrat"
version("Alpha.1")

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlin-js-wrappers")
    maven("https://kotlin.bintray.com/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlin-dev")
    maven("https://kotlin.bintray.com/kotlinx")
}

configure<org.carrat.webidl.build.fetch.gradle.FetchWebIdlExtension> {
    getSources().set(projectDir.resolve("src/widl/sources/sources.yml").absolutePath)
    getOutput().set(projectDir.resolve("src/widl/src").absolutePath)
}

configure<org.carrat.webidl.build.compile.gradle.CompileWebIdlExtension> {
    getInput().set(projectDir.resolve("src/widl/src").absolutePath)
    getCommonOutput().set(buildDir.resolve("src/generatedSrc/widl/commonMain").absolutePath)
    getJsOutput().set(buildDir.resolve("src/generatedSrc/widl/jsMain").absolutePath)
    getOtherOutputs().set(mapOf("jvmMain" to buildDir.resolve("src/generatedSrc/widl/jvmMain").absolutePath))
    getPackage().set("org.carrat.webapi")
}

kotlin {
    explicitApi()
    jvm()
    js(BOTH).browser()
    js(BOTH).useCommonJs()
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation(kotlin("stdlib-common"))
                implementation(kotlin("serialization"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
            }
        }
        js(BOTH).compilations["main"].defaultSourceSet {
            dependencies {
                implementation(npm("history", "4.10.1"))
            }
        }
    }
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion")
    }
    dependencies {
        dependency("org.jetbrains.kotlinx:kotlinx-html:0.7.2")
        dependency("org.jetbrains:kotlin-css:1.0.0-$kotlinWrappersVersion")
        dependency("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
        dependency("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
        dependency("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.4.1")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
        dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.1")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}



publishing {
    publications {
        forEach {
            if(it is MavenPublication) {
                it.pom {
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }
    repositories {
//        maven {
//            val releasesRepoUrl = "https://api.bintray.com/maven/carrat/carrat/${project.name}/;publish=1"
//            val snapshotsRepoUrl = "https://api.bintray.com/maven/carrat/carrat-snapshots/${project.name}/;publish=1;override=1"
//            val isSnapshot = version.toString().endsWith("SNAPSHOT")
//            name = if(isSnapshot) "carratSnapshot" else "carrat"
//            url = uri(if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl)
//            credentials(PasswordCredentials::class)
//        }
//        maven {
//            name = "github"
//            url = uri("https://maven.pkg.github.com/carrat/carrat-webapi")
//            credentials(PasswordCredentials::class)
//        }
    }
}
