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