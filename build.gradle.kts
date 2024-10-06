allprojects {
    group = "net.darkmeow"
    version = "1.0.1006"
}

plugins {
    id("java")
}

subprojects {
    apply {
        plugin("java")
    }

    repositories {
        mavenCentral()
    }

    base {
        archivesName.set("${rootProject.name}-${project.name}")
    }
    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }
    }
}


tasks.jar.get().isEnabled = false
