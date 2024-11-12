rootProject.name = "mod-loader"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://darkmeowteam.github.io/maven/")
        mavenLocal()
    }

    val kotlinVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}

include("runtime", "plugin")