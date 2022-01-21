plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`

    // dokka
    id("net.ltgt.errorprone") version "2.0.2"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
    id("org.jmailen.kotlinter") version "3.7.0"
}

group = "pro.streem"
version = "0.1.0-SNAPSHOT"

kotlin {
    explicitApi()

    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(11))
    }

    target {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
                javaParameters = true
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.nimbusds:nimbus-jose-jwt:9.15.2")

    testImplementation(kotlin("test"))

    errorprone("com.google.errorprone:error_prone_core:2.10.0")
}