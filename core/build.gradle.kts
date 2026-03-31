plugins {
    id("java-library")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.codemc.io/repository/maven-releases/") }
    maven { url = uri("https://repo.codemc.io/repository/maven-snapshots/") }
}

dependencies {
    compileOnly("com.github.retrooper:packetevents-spigot:2.8.0")
    compileOnly("org.spongepowered:configurate-core:4.2.0")
    compileOnly("org.spongepowered:configurate-yaml:4.2.0")

    implementation(project(":locatables"))
    compileOnly(project(":logging"))
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}
