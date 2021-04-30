import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.carrat.webidl.build.version
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    val kotlinVersion by extra("1.5.0")

    dependencies {
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
    }
}

val kotlinVersion by extra("1.5.0")
val kotlinWrappersVersion by extra("pre.134-kotlin-1.4.21")
val kotlinxSerializationVersion by extra("1.0.1")

plugins {
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("maven-publish")
    id("fetchWebIdl")
    id("compileWebIdl")
    kotlin("multiplatform")
}

group = "org.carrat"
version("0.0alpha0")

repositories {
    mavenCentral()
    jcenter()
    maven("https://kotlin.bintray.com/kotlin-js-wrappers")
    maven("https://kotlin.bintray.com/kotlin-eap")
    maven("https://kotlin.bintray.com/kotlin-dev")
    maven("https://kotlin.bintray.com/kotlinx")
    maven("https://carrat.jfrog.io/artifactory/carrat-dev/")
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
    getPackage().set("org.carrat.web.webapi")
    getIgnoreDeclarations().add("Function")
}

kotlin {
    explicitApi()
    jvm()
    js(BOTH).browser()
    js(BOTH).useCommonJs()
    sourceSets {
        commonMain {
            dependencies {
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
            }
        }
        js(BOTH).compilations["main"].defaultSourceSet {
            dependencies {
            }
        }
    }
}

configure<DependencyManagementExtension> {
    imports {
        mavenBom("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion")
    }
    dependencies {
    }
}

tasks.withType<KotlinJvmCompile> {
    kotlinOptions.useIR = true
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}


publishing {
    publications {
        forEach {
            if (it is MavenPublication) {
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
        maven {
            name = "carrat"
            url = uri("https://carrat.jfrog.io/artifactory/carrat-dev/")
            credentials(PasswordCredentials::class)
        }
    }
}
