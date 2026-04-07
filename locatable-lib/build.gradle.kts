plugins {
    id("java")
}

group = "games.cubi.locatables"
version = "0.1.0"

val commitShort = providers.exec {
    commandLine("git", "rev-parse", "--short=8", "HEAD")
}.standardOutput.asText.map { it.trim() }

val commitFull = providers.exec {
    commandLine("git", "rev-parse", "HEAD")
}.standardOutput.asText.map { it.trim() }

val buildTime = providers.exec {
    commandLine("date", "-u", "+%Y-%m-%dT%H:%M:%SZ")
}.standardOutput.asText.map { it.trim() }

tasks {
    processResources {
        val gitProps = mapOf(
            "short_git" to commitShort.get(),
            "long_git" to commitFull.get(),
            "build_time" to buildTime.get(),
            "version" to version.toString()
        )
        // build-properties is not used by the locatables library itself, but is included in the jar so that platforms can access it.
        filesMatching("build-properties/locatable-lib.yml") {
            expand(gitProps)
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":logging"))
}