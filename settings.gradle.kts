rootProject.name = "mod-loader"

pluginManagement {
    repositories {
        mavenLocal()

        gradlePluginPortal()
        maven("https://darkmeow.nekocurit.asia/maven/") // https://darkmeowteam.github.io/maven/
    }

    val kotlinVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

include("runtime", "plugin")