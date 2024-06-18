import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    kotlin("jvm")
    id("maven-publish")
}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.minecraftforge.net/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    implementation("org.apache.commons:commons-compress:1.25.0")

    implementation("com.google.code.gson:gson:2.10")
    implementation("org.ow2.asm:asm-commons:9.4")
    implementation("org.ow2.asm:asm-tree:9.4")
}

gradlePlugin {
    plugins {
        create("mod-loader-plugin") {
            id = "net.darkmeow.mod-loader-plugin"
            displayName = "mod-loader-plugin"
            description = "Allows packaging mod implementations for different platform into the same Jar"
            implementationClass = "net.darkmeow.loader.ModLoaderPlugin"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

tasks {
    withType(KotlinCompile::class.java) {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    processResources {
        expand("version" to project.version)
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "net.darkmeow"
            artifactId = "mod-loader-plugin"
            version = "1.0"
        }
    }
    repositories {
        mavenLocal()
    }
}