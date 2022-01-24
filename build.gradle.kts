plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
    signing

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

// region Publishing configuration

val sonatypeApiUser = providers.gradlePropertyOrEnvironmentVariable("sonatypeApiUser")
val sonatypeApiKey = providers.gradlePropertyOrEnvironmentVariable("sonatypeApiKey")
val sonatypeRepositoryId = providers.gradlePropertyOrEnvironmentVariable("sonatypeRepositoryId")
val publishToSonatype = sonatypeApiUser.isPresent && sonatypeApiKey.isPresent
if (!publishToSonatype) {
    logger.info("Sonatype API key not defined, skipping configuration of Maven Central publishing repository")
}

val signingKeyAsciiArmored = providers.gradlePropertyOrEnvironmentVariable("signingKeyAsciiArmored")
if (signingKeyAsciiArmored.isPresent) {
    signing {
        @Suppress("UnstableApiUsage")
        useInMemoryPgpKeys(signingKeyAsciiArmored.get(), "")
        sign(publishing.publications)
    }
} else {
    logger.info("PGP signing key not defined, skipping signing configuration")
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("streemServerSdk") {
            from(components["java"])
        }
    }

    if (publishToSonatype) {
        repositories {
            maven {
                name = "sonatype"
                url = when {
                    project.version.toString().endsWith("-SNAPSHOT") ->
                        uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                    !sonatypeRepositoryId.isPresent ->
                        throw IllegalStateException("Sonatype Repository ID must be provided for non-SNAPSHOT builds")
                    else ->
                        uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/${sonatypeRepositoryId.get()}")
                }

                credentials {
                    username = sonatypeApiUser.get()
                    password = sonatypeApiKey.get()
                }
            }
        }
    }
}

// endregion Publishing configuration