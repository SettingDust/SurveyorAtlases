@file:Suppress("UnstableApiUsage")

import com.google.devtools.ksp.gradle.KspAATask
import earth.terrarium.cloche.IncludeTransformationState
import earth.terrarium.cloche.RemapNamespaceAttribute
import earth.terrarium.cloche.api.target.FabricTarget
import groovy.lang.Closure
import net.msrandom.minecraftcodev.core.utils.asNamePart
import net.msrandom.minecraftcodev.core.utils.lowerCamelCaseGradleName

plugins {
    java

    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"

    id("com.palantir.git-version") version "3.1.0"

    id("com.gradleup.shadow") version "8.3.6"

    id("earth.terrarium.cloche") version "0.11.20"
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
            includeGroup("org.ladysnake.cardinal-components-api")
        }
    }

    maven("https://maven.parchmentmc.org") {
        content {
            includeGroup("org.parchmentmc.data")
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
            required = true
            version {
                start = "1.20.1"
            }
        }

        dependency {
            modId = "moonlight"
            required = true
        }

        dependency {
            modId = "surveyor"
            required = true
        }
    }

    mappings {
        official()
    }

    common {
        mixins.from(file("src/common/main/resources/$id.mixins.json"))

        dependencies {
            compileOnly("org.spongepowered:mixin:0.8.7")
        }
    }

    val commons = mapOf(
        "1.20.1" to common("common:1.20.1"),
        "1.21.1" to common("common:1.21.1"),
    )

    val fabricCommon = common("fabric:common")

    targets.withType<FabricTarget> {
        dependsOn(fabricCommon)

        loaderVersion = "0.16.14"

        includedClient()

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

        dependencies {
            modImplementation("net.fabricmc:fabric-language-kotlin:1.13.1+kotlin.2.1.10")
        }
    }

    fabric("fabric:1.20.1") {
        minecraftVersion = "1.20.1"

        dependencies {
            fabricApi("0.92.6")

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

    fabric("fabric:1.21") {
        minecraftVersion = "1.21.1"

        dependencies {
            fabricApi("0.116.4")

            modImplementation(catalog.surveyor.get1().get21()) {
                attributes {
                    attribute(IncludeTransformationState.ATTRIBUTE, IncludeTransformationState.Extracted)
                }
            }
            modImplementation(catalog.surveystones.get1().get21())

            modImplementation(catalog.moonlight.get1().get21().fabric)
            modImplementation(catalog.supplementaries.get1().get21().fabric)
            modImplementation(catalog.mapAtlases.get1().get21().fabric)
            modRuntimeOnly(catalog.cardinalComponentsApi.base.get1().get21())
            modRuntimeOnly(catalog.cardinalComponentsApi.item.get1().get21())
        }
    }

    forge {
        minecraftVersion = "1.20.1"
        loaderVersion = "47.4.4"

        metadata {
            modLoader = "kotlinforforge"
            loaderVersion {
                start = "4"
            }

            dependency {
                modId = "fabric_api"
            }
        }

        repositories {
            maven("https://repo.spongepowered.org/maven") {
                content {
                    includeGroup("org.spongepowered")
                }
            }
        }

        mappings {
            fabricIntermediary()
        }

        dependencies {
            implementation("org.spongepowered:mixin:0.8.7")
            implementation(catalog.mixinextras.forge) {
                attributes {
                    attribute(IncludeTransformationState.ATTRIBUTE, IncludeTransformationState.Extracted)
                }
            }

            modImplementation("thedarkcolour:kotlinforforge:4.10.0")


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
        }
    }

    neoforge("neoforge:1.21") {
        minecraftVersion = "1.21.1"
        loaderVersion = "21.1.192"

        metadata {
            modLoader = "kotlinforforge"
            loaderVersion {
                start = "5"
            }
        }

        mappings {
            fabricIntermediary()
        }

        dependencies {
            modImplementation("thedarkcolour:kotlinforforge:5.9.0")

            modImplementation(catalog.surveyor.get1().get21()) {
                attributes {
                    attribute(RemapNamespaceAttribute.ATTRIBUTE, RemapNamespaceAttribute.INTERMEDIARY)
                }
            }
            modImplementation(catalog.surveystones.get1().get21()) {
                attributes {
                    attribute(RemapNamespaceAttribute.ATTRIBUTE, RemapNamespaceAttribute.INTERMEDIARY)
                }
            }

            modImplementation(catalog.moonlight.get1().get21().neoforge)
            modImplementation(catalog.supplementaries.get1().get21().neoforge)
            modImplementation(catalog.mapAtlases.get1().get21().neoforge)
        }
    }

    targets.all {
        dependsOn(commons.getValue(minecraftVersion.get()))

        runs {
            client()
        }

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

    val serviceLoaderVersion = "0.0.19"

    "app.softwork.serviceloader:ksp-plugin:$serviceLoaderVersion".let { dependency ->
        targets.all target@{
            dependencies {
                implementation(dependency) {
                    exclude(module = "kotlin-stdlib")
                }
                implementation("app.softwork.serviceloader:runtime:$serviceLoaderVersion") {
                    exclude(module = "kotlin-stdlib")
                }
            }

            sourceSet.apply sourceSet@{
                project.dependencies {
                    kspConfigurationName(dependency) {
                        exclude(module = "kotlin-stdlib")
                    }
                }

                resources.srcDir(layout.buildDirectory.file("generated/ksp/${sourceSet.name}/resources"))

                afterEvaluate {
                    if (tasks.findByName(kspKotlinTaskName) != null) {
                        tasks.named<KspAATask>(kspKotlinTaskName) task@{
                            this@sourceSet.resources.srcDir(this@task.kspConfig.resourceOutputDir)

                            this@target.dependsOn.all dependency@{
                                this@task.dependsOn(this@dependency.sourceSet.kspKotlinTaskName)
                            }
                        }

                        tasks.named(processResourcesTaskName) task@{
                            dependsOn(kspKotlinTaskName)
                        }
                    }
                }
            }
        }

        commonTargets.all target@{
            dependencies {
                implementation(dependency) {
                    exclude(module = "kotlin-stdlib")
                }
                implementation("app.softwork.serviceloader:runtime:$serviceLoaderVersion") {
                    exclude(module = "kotlin-stdlib")
                }
            }

            sourceSet.apply sourceSet@{
                project.dependencies {
                    kspConfigurationName(dependency) {
                        exclude(module = "kotlin-stdlib")
                    }
                }

                afterEvaluate {
                    if (tasks.findByName(kspKotlinTaskName) != null) {
                        tasks.named<KspAATask>(kspKotlinTaskName) task@{
                            this@sourceSet.resources.srcDir(this@task.kspConfig.resourceOutputDir)

                            this@target.dependsOn.all dependency@{
                                this@task.dependsOn(this@dependency.sourceSet.kspKotlinTaskName)
                            }
                        }

                        tasks.named(processResourcesTaskName) task@{
                            dependsOn(kspKotlinTaskName)
                        }
                    }
                }
            }
        }
    }
}

tasks {
    withType<ProcessResources> {
        duplicatesStrategy = DuplicatesStrategy.WARN
    }
}

val SourceSet.kspKotlinTaskName: String
    get() = lowerCamelCaseGradleName("ksp", name.asNamePart, "kotlin")

val SourceSet.kspConfigurationName: String
    get() = lowerCamelCaseGradleName("ksp", name.asNamePart)