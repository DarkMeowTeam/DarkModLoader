rootProject.name = "mod-loader"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://darkmeowteam.github.io/maven/")
        mavenLocal()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm").version("2.0.0")
    }
}

include("runtime", "plugin")