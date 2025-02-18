rootProject.name = "CafeMC"

pluginManagement { 
    repositories {
        mavenCentral()
        gradlePluginPortal()

        repositories {
            maven {
                name = "localPlugins"
                url = uri("${System.getProperty("user.home")}/.gradle_plugins")
            }
        }
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
include("migrate")
