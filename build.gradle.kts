allprojects {
    group = "net.darkmeow"
    version = "1.1.0216"
}

plugins {
    id("java")
}

subprojects {
    apply {
        plugin("java")
    }

    repositories {
        mavenLocal()
        maven("https://maven.aliyun.com/repository/central/") // mavenCentral()

        maven("https://maven-minecraft.mirror.nekocurit.asia/") // https://maven.minecraftforge.net/
        maven("https://libraries.minecraft.net/")
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
