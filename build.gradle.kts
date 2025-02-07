plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("dev.lizainslie.mcdevserver") version "0.1.0"
}

group = "dev.lizainslie"
version = "0.1.1"

val minecraftVersion = "1.21.4"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") {
        name = "spigotmc-repo"
    }
    
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    
    maven("https://mvn.devos.one/releases") {
        name = "devOS"
    }

    maven("https://jitpack.io")

}

val exposedVersion: String by project

dependencies {
    // platform
    compileOnly("org.spigotmc:spigot-api:1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // orm
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    
    // db
    implementation("org.xerial:sqlite-jdbc:3.44.1.0")

    compileOnly("com.github.NEZNAMY", "TAB-API", "5.0.4")
    // integration
    compileOnly("com.github.NEZNAMY:TAB-API:5.0.4")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

mcDevServer {
    minecraftVersion = "1.21.4"
    serverDirectory = "${project.rootDir}/devServer"
}
