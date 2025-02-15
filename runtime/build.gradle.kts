plugins {
    id("net.darkmeow.jar-optimizer") version "1.0.1113"
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("maven-publish")
}

val configureSourceSet: SourceSet.() -> Unit = {
    compileClasspath += sourceSets.main.get().output
    compileClasspath += sourceSets.main.get().compileClasspath

    runtimeClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
}

val legacyForge by sourceSets.creating(configureSourceSet)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation("org.tukaani:xz:1.9")

    "legacyForgeCompileOnly"("net.minecraft:launchwrapper:1.12") { isTransitive = false }
    "legacyForgeCompileOnly"("net.minecraftforge:forge:1.12.2-14.23.5.2860:universal") { isTransitive = false }
}

tasks {
    fun JavaCompile.setCompilerVersion(version: Int) {
        val fullVersion = if (version <= 8) "1.$version" else version.toString()
        this.sourceCompatibility = fullVersion
        this.targetCompatibility = fullVersion
        javaToolchains {
            javaCompiler.set(compilerFor { languageVersion.set(JavaLanguageVersion.of(version)) })
        }
    }

    withType(JavaCompile::class) { setCompilerVersion(8) }

    shadowJar {
        configurations = listOf(project.configurations.runtimeClasspath.get())

        relocate("org.tukaani", "net.darkmeow.loader")
    }
}


val legacyForgeJar by tasks.creating(Jar::class) {
    archiveClassifier.set("legacy-forge")
    from(legacyForge.output)
}

val optimizeJar = jarOptimizer.register(tasks.shadowJar, "net.darkmeow.loader")

artifacts {
    archives(optimizeJar)
    archives(legacyForgeJar)
}


publishing {
    publications {
        create<MavenPublication>("runtime") {
            artifactId = "mod-loader-runtime"
            artifact(optimizeJar) {
                classifier = ""
            }
            artifact(legacyForgeJar)
        }
    }
    repositories {
        mavenLocal()
    }
}
