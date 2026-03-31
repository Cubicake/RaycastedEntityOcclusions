plugins {
    id("java")
}

group = "games.cubi.locatables"
version = "beta-1.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":logging"))
}