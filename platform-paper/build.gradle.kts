plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

dependencies {
    paperweight.paperDevBundle("1.21.11-R0.1-SNAPSHOT")
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    compileOnly("org.spongepowered:configurate-core:4.2.0")
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")

    implementation("org.jetbrains:annotations:24.0.1")

    implementation(project(":locatables"))
    compileOnly(project(":logging"))
    compileOnly("com.mojang:brigadier:1.3.10")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

group = "games.cubi.raycastedantiesp.paper"
version = "2.0.0-alpha-3"

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.11")
        jvmArgs("-Xms4G", "-Xmx4G", "-Dcom.mojang.eula.agree=true")
    }

    processResources {
        val props = mapOf("version" to version)
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

tasks.jar {
    archiveBaseName.set("RaycastedAntiESP-Paper")
}
