@file:Suppress("UnstableApiUsage")

//import earth.terrarium.cloche.IncludeTransformationState
import earth.terrarium.cloche.IncludeTransformationState
import earth.terrarium.cloche.RemapNamespaceAttribute
import earth.terrarium.cloche.api.target.FabricTarget
import groovy.lang.Closure

plugins {
    java

    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"

    id("com.palantir.git-version") version "3.1.0"

    id("com.gradleup.shadow") version "8.3.6"

    id("earth.terrarium.cloche") version "0.11.5"
}

val archive_name: String by rootProject.properties
val id: String by rootProject.properties
val source: String by rootProject.properties

group = "settingdust.surveyor_atlases"

val gitVersion: Closure<String> by extra
version = gitVersion()

base { archivesName = archive_name }

repositories {
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven")
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

    exclusiveContent {
        forRepository {
            maven("https://maven.su5ed.dev/releases")
        }
        filter {
            includeGroupAndSubgroups("dev.su5ed")
            includeGroupAndSubgroups("org.sinytra")
        }
    }

    maven("https://modmaven.dev") {
        content {
            includeGroup("mezz.jei")
        }
    }

    maven("https://thedarkcolour.github.io/KotlinForForge/") {
        content {
            includeGroup("thedarkcolour")
        }
    }

    maven("https://maven.ladysnake.org/releases") {
        content {
            includeGroup("dev.onyxstudios.cardinal-components-api")
        }
    }

    mavenCentral()

    cloche {
        librariesMinecraft()
        main()
        mavenFabric()
        mavenForge()
        mavenNeoforged()
        mavenNeoforgedMeta()
        mavenParchment()
    }

    mavenLocal()
}


cloche {
    metadata {
        modId = id
        name = rootProject.property("name").toString()
        description = rootProject.property("description").toString()
        license = "CC-BY-SA 4.0"
        icon = "assets/$id/icon.png"
        sources = source
        issues = "$source/issues"
        author("SettingDust")

        dependency {
            modId = "minecraft"
            version {
                start = "1.20.1"
            }
        }

        dependency {
            modId = "moonlight"
        }

        dependency {
            modId = "surveyor"
        }
    }

    mappings {
        official()
    }

    common {
        mixins.from(file("src/common/main/resources/surveyor_atlases.mixins.json"))

        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.7")
        }
    }

    fabric {
        minecraftVersion = "1.20.1"

        metadata {
            entrypoint("main") {
                adapter = "kotlin"
                value = "settingdust.surveyor_atlases.fabric.EntrypointKt::init"
            }

            entrypoint("client") {
                adapter = "kotlin"
                value = "settingdust.surveyor_atlases.fabric.EntrypointKt::clientInit"
            }

            dependency {
                modId = "fabric-api"
            }

            dependency {
                modId = "fabric-language-kotlin"
            }
        }

        runs {
            client()
        }

        dependencies {
            fabricApi("0.92.6")

            modImplementation("net.fabricmc:fabric-language-kotlin:1.13.1+kotlin.2.1.10")

            modImplementation(catalog.surveyor.get1().get20()) {
                attributes {
                    attribute(IncludeTransformationState.ATTRIBUTE, IncludeTransformationState.Extracted)
                }
            }
            modImplementation(catalog.surveystones.get1().get20())

            modImplementation(catalog.moonlight.get1().get20().fabric)
            modImplementation(catalog.supplementaries.get1().get20().fabric)
            modImplementation(catalog.mapAtlases.get1().get20().fabric)
            modRuntimeOnly(catalog.cardinalComponentsApi.base.get1().get20())
            modRuntimeOnly(catalog.cardinalComponentsApi.item.get1().get20())
        }
    }

    forge {
        minecraftVersion = "1.20.1"
        loaderVersion = "47.3.29"

        metadata {
            modLoader = "kotlinforforge"
            loaderVersion {
                start = "4"
            }

            dependency {
                modId = "fabric_api"
            }
        }

        runs {
            client()
        }

        mappings {
            fabricIntermediary()
        }

        repositories {
            maven("https://repo.spongepowered.org/maven") {
                content {
                    includeGroup("org.spongepowered")
                }
            }
        }

        dependencies {
            implementation("org.spongepowered:mixin:0.8.7")
            implementation(catalog.mixinextras.forge) {
                attributes {
                    attribute(IncludeTransformationState.ATTRIBUTE, IncludeTransformationState.Extracted)
                }
            }

            modImplementation("thedarkcolour:kotlinforforge:4.10.0")

            modImplementation("dev.su5ed.sinytra.fabric-api:fabric-api:0.92.2+1.11.11+1.20.1")

            modImplementation(catalog.surveyor.get1().get20()) {
                attributes {
                    attribute(RemapNamespaceAttribute.ATTRIBUTE, RemapNamespaceAttribute.INTERMEDIARY)
                }
            }
            modImplementation(catalog.surveystones.get1().get20()) {
                attributes {
                    attribute(RemapNamespaceAttribute.ATTRIBUTE, RemapNamespaceAttribute.INTERMEDIARY)
                }
            }

            modImplementation(catalog.moonlight.get1().get20().forge)
            modImplementation(catalog.supplementaries.get1().get20().forge)
            modImplementation(catalog.mapAtlases.get1().get20().forge)
            modRuntimeOnly(catalog.cardinalComponentsApi.base.get1().get20()) {
                isTransitive = false
                attributes {
                    attribute(RemapNamespaceAttribute.ATTRIBUTE, RemapNamespaceAttribute.INTERMEDIARY)
                }
            }
            modRuntimeOnly(catalog.cardinalComponentsApi.item.get1().get20()) {
                isTransitive = false
                attributes {
                    attribute(RemapNamespaceAttribute.ATTRIBUTE, RemapNamespaceAttribute.INTERMEDIARY)
                }
            }
        }
    }

    targets.withType<FabricTarget> {
        loaderVersion = "0.16.14"

        includedClient()
    }

    targets.all {
        mappings {
            parchment(minecraftVersion.map {
                when (it) {
                    "1.20.1" -> "2023.09.03"
                    "1.21.1" -> "2024.11.17"
                    else -> throw IllegalArgumentException("Unsupported minecraft version $it")
                }
            })
        }
    }
}

tasks {
    withType<ProcessResources> {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}