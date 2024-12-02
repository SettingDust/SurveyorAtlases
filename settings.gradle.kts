dependencyResolutionManagement {
    pluginManagement {
        repositories {
            maven("https://maven.wagyourtail.xyz/releases")
            mavenCentral()
            gradlePluginPortal()
        }
    }
}

dependencyResolutionManagement.versionCatalogs.create("catalog") {
    // https://github.com/palantir/gradle-git-version
    plugin("git-version", "com.palantir.git-version").version("3.+")

    plugin("shadow", "com.gradleup.shadow").version("8.+")

    plugin("unmined", "xyz.wagyourtail.unimined").version("1.+")


    val minecraft = "1.20.1"
    version("minecraft", minecraft)


    val kotlin = "2.0.20"
    version("kotlin", kotlin)
    plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").version(kotlin)
    plugin("kotlin-plugin-serialization", "org.jetbrains.kotlin.plugin.serialization").version(kotlin)

    library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").version(kotlin)

    val kotlinxSerialization = "1.7.3"
    library("kotlinx-serialization-core", "org.jetbrains.kotlinx", "kotlinx-serialization-core").version(
        kotlinxSerialization
    )
    library("kotlinx-serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version(
        kotlinxSerialization
    )

    library("kotlinx-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core").version("1.9.0")

    // https://modrinth.com/mod/kinecraft-serialization/versions
    library("kinecraft-serialization", "maven.modrinth", "kinecraft-serialization").version("1.16.0")

    // https://linkie.shedaniel.dev/dependencies?loader=fabric
    version("fabric-loader", "0.16.7")
    version("fabric-api", "0.92.2+$minecraft")
    library("fabric-kotlin", "net.fabricmc", "fabric-language-kotlin").version("1.12.2+kotlin.$kotlin")

    // https://linkie.shedaniel.dev/dependencies?loader=forge
    version("lexforge", "47.3.11")
    library("forgified-fabric-api", "dev.su5ed.sinytra.fabric-api", "fabric-api").version("0.92.2+1.11.8+$minecraft")
    library("sinytra-connector", "org.sinytra", "Connector").version("1.0.0-beta.46+$minecraft")
    library("kotlin-forge", "thedarkcolour", "kotlinforforge").version("4.11.0")

    library("mixin", "org.spongepowered", "mixin").version("0.8.7")
    val mixinextras = "0.5.0-beta.2"
    library("mixinextras-common", "io.github.llamalad7", "mixinextras-common").version(mixinextras)
    library("mixinextras-lexforge", "io.github.llamalad7", "mixinextras-forge").version(mixinextras)
    library("mixinextras-fabric", "io.github.llamalad7", "mixinextras-fabric").version(mixinextras)

    library("surveyor", "folk.sisby", "surveyor").version("0.6.25+1.20")
    library("surveystones", "maven.modrinth", "surveystones").version("1.3.1+1.20")

    library("moonlight-fabric", "maven.modrinth", "moonlight").version("fabric_1.20-2.13.10")
    library("moonlight-forge", "maven.modrinth", "moonlight").version("forge_1.20-2.13.10")

    // https://www.curseforge.com/minecraft/mc-mods/map-atlases/files/all?page=1&pageSize=20
    library("map-atlases-fabric", "curse.maven", "map-atlases-436298").version("5626092")
    // https://www.curseforge.com/minecraft/mc-mods/map-atlases-forge/files/all?page=1&pageSize=20
    library("map-atlases-forge", "curse.maven", "map-atlases-forge-519759").version("5626093")

    library("supplementaries-fabric", "maven.modrinth", "supplementaries").version("1.20-2.8.17-fabric")
    library("supplementaries-forge", "maven.modrinth", "supplementaries").version("1.20-2.8.17-forge")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

val name: String by settings

rootProject.name = name