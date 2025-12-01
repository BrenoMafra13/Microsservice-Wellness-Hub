plugins {
    java
    id("org.springframework.boot") version "3.5.7" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

group = "ca.gbc.comp3095"
version = "0.0.1-SNAPSHOT"
description = "microsservices-parent"

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks.register("prepareKotlinBuildScriptModel") {}
}
