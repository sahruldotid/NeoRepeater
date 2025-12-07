plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.portswigger.burp.extensions:montoya-api:2025.11")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "dev.syahrul.NeoRepeater"
}

version = "v1.0.0"

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    
    archiveBaseName.set("NeoRepeater")
    archiveVersion.set(version.toString())

    manifest {
        attributes(
            "Main-Class" to "dev.syahrul.NeoRepeater"
        )
    }

    from({
        configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
    })
}