plugins {
    application
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.6.10"
}

group = "pro.streem.examples"
version = "0.0.1"

application {
    mainClass.set("pro.streem.examples.tokenserver.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    val ktorVersion = "1.6.7"
    val streemSdkVersion = "0.2.0"

    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-auth:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:1.2.10")

    implementation("pro.streem:streem-server-sdk:$streemSdkVersion")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
}
