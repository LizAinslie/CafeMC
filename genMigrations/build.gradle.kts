plugins {
    kotlin("jvm")
}

group = "dev.lizainslie"
version = "0.1.1"

repositories {
    mavenCentral()
}

val exposedVersion: String by rootProject

dependencies {
    testImplementation(kotlin("test"))
    implementation(rootProject)
    implementation("org.jetbrains.exposed:exposed-migration:$exposedVersion")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
