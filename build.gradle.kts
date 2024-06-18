allprojects {
    group = "net.darkmeow"
    version = "1.0"
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
}


tasks.jar.get().isEnabled = false
