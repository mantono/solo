plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70" apply true
    id("idea")
    id("java-library")
    id("maven-publish")
}

group = "com.mantono"
version = "2.0.0"
description = "Non-synchronized unique ID generator"

defaultTasks("test")

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

val jvmVersion = "11"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.3.70")

    val coroutines = "1.3.3"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:$coroutines")

    implementation("com.github.mantono:pyttipanna:1.0.0")

    // Junit
    val junit = "5.6.0"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junit")
}

publishing {
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/mantono/${project.name}")
            credentials {
                username = "mantono"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        register("gpr", MavenPublication::class) {
            this.artifactId = project.name
            this.groupId = project.group.toString()
            this.version = project.version.toString()
            from(components["java"])
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks {
    test {
        useJUnitPlatform()

        // Show test results.
        testLogging {
            events("passed", "skipped", "failed")
        }
        reports {
            junitXml.isEnabled = false
            html.isEnabled = true
        }
    }

    compileKotlin {
        sourceCompatibility = jvmVersion
        kotlinOptions {
            jvmTarget = jvmVersion
        }
    }

    wrapper {
        description = "Generates gradlew[.bat] scripts for faster execution"
        gradleVersion = "6.2.1"
    }
}