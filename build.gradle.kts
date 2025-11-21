plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("io.spring.dependency-management") version "1.1.7"
    `maven-publish`
    `java-library`
}

group = "wtf.milehimikey"
version = "1.0.2"
description = "Axon Framework JPA Connector Library"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.4.4")
    }
}

dependencies {
    // Spring Boot Starters
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-autoconfigure")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Jackson for JSON handling
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Database drivers (optional)
    compileOnly("org.postgresql:postgresql")
    compileOnly("com.mysql:mysql-connector-j")
    compileOnly("com.h2database:h2")

    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("Axon JPA Connector")
                description.set("A Spring Boot library providing JPA entities and repositories for Axon Framework")
                url.set("https://github.com/milehimikey/axon-jpa-connector")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("milehimikey")
                        name.set("MiKey")
                        email.set("milehimikey@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/milehimikey/axon-jpa-connector.git")
                    developerConnection.set("scm:git:ssh://github.com:milehimikey/axon-jpa-connector.git")
                    url.set("https://github.com/milehimikey/axon-jpa-connector")
                }
            }
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/milehimikey/axon-jpa-connector")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_ACTOR")
                password = project.findProperty("gpr.token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
