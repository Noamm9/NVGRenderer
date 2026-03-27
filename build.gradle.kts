import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.10"
    id("fabric-loom") version "1.15-SNAPSHOT"
    id("maven-publish")
}

val targetJavaVersion = 21
group = project.property("maven_group") !!
version = project.property("mod_version") !!

base {
    archivesName.set(project.property("archives_base_name") as String)
}

repositories {
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

dependencies {
    minecraft("com.mojang:minecraft:${project.property("minecraft_version") !!}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version") !!}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version") !!}")

    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version") !!}")
    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.2")

    val lwjglVersion = "3.3.3"

    modImplementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion")
    include("org.lwjgl:lwjgl-nanovg:$lwjglVersion")

    listOf("windows", "linux", "macos", "macos-arm64").forEach { os ->
        modImplementation("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-$os")
        include("org.lwjgl:lwjgl-nanovg:$lwjglVersion:natives-$os")
    }

    testImplementation(kotlin("test"))
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version") !!)
    inputs.property("loader_version", project.property("loader_version") !!)
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand("version" to project.version, "minecraft_version" to project.property("minecraft_version") !!, "loader_version" to project.property("loader_version") !!, "kotlin_loader_version" to project.property("kotlin_loader_version") !!)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.jar {
    from("LICENSE.txt") {
        rename { "LICENSE_${project.base.archivesName.get()}.txt" }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = project.property("archives_base_name") as String
            from(components["java"])
        }
    }
}
