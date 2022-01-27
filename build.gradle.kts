import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
    signing

    id("net.ltgt.errorprone") version "2.0.2"
    id("org.ajoberstar.git-publish") version "3.0.0"
    id("org.jetbrains.dokka") version "1.6.10"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.8.0"
    id("org.jmailen.kotlinter") version "3.7.0"
}

group = "pro.streem"

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
    withSourcesJar()
}

val dokkaJavadocJar by tasks.register<Jar>("dokkaJavadocJar") {
    dependsOn(tasks.dokkaJavadoc)
    group = "Documentation"
    from(tasks.dokkaJavadoc.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

val dokkaHtmlJar by tasks.register<Jar>("dokkaHtmlJar") {
    dependsOn(tasks.dokkaHtml)
    group = "Documentation"
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("html-doc")
}

publishing {
    publications {
        create<MavenPublication>("streemServerSdk") {
            from(components["java"])

            artifact(dokkaJavadocJar)
            artifact(dokkaHtmlJar)

            pom {
                name.set(artifactId)
                description.set("Streem Server SDK for Java & Kotlin")
                url.set("https://github.com/streem/streem-sdk-jvm")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                organization {
                    name.set("Streem, LLC")
                    url.set("https://github.com/streem")
                }

                developers {
                    developer {
                        id.set("streem")
                        name.set("Streem, LLC")
                        url.set("https://github.com/streem")
                    }
                }

                scm {
                    connection.set("scm:git:git@github.com:streem/streem-sdk-jvm.git")
                    developerConnection.set("scm:git:git@github.com:streem/streem-sdk-jvm.git")
                    url.set("git@github.com:streem/streem-sdk-jvm.git")
                }
            }
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

val dokkaJavaHtml by tasks.registering(DokkaTask::class) {
    dependencies {
        plugins("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.10")
    }
}

gitPublish {
    repoUri.set("https://github.com/streem/streem-sdk-jvm.git")
    branch.set("gh-pages")
    contents {
        from(tasks.dokkaHtml.flatMap { it.outputDirectory }) {
            into("kotlin")
        }
        from(dokkaJavaHtml.flatMap { it.outputDirectory }) {
            into("java")
        }
    }
}

// endregion Publishing configuration