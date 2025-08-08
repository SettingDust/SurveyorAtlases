enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
    pluginManagement {
        repositories {
            mavenCentral()
            gradlePluginPortal()
            maven("https://maven.msrandom.net/repository/cloche")
            mavenLocal()
        }
    }
}

object VersionFormats {
    val versionPlusMc = { mcVer: String, ver: String -> "$ver+$mcVer" }
    val mcDashVersion = { mcVer: String, ver: String -> "$mcVer-$ver" }
}

object VersionTransformers {
    val versionDashLoader = { ver: String, loader: String -> "$ver-$loader" }
    val loaderUnderlineVersion = { ver: String, loader: String -> "${loader}_$ver" }
}

open class LoaderConfig(
    val slugTransformer: (String) -> String = { it },
    val versionTransformer: (String, String) -> String = { ver, _ -> ver }
) {
    companion object : LoaderConfig()

    constructor(slugTransformer: (String) -> String) : this(slugTransformer, { ver, _ -> ver })
    constructor(versionTransformer: (String, String) -> String) : this(
        { it },
        versionTransformer
    )
}

data class LoaderMapping(
    val mcVersion: String,
    val loaders: Map<String, LoaderConfig>
)

fun VersionCatalogBuilder.modrinth(
    id: String,
    slug: String = id,
    mcVersionToVersion: Map<String, String>,
    versionFormat: (String, String) -> String = { _, v -> v },
    mapping: List<LoaderMapping> = emptyList()
) {
    val allLoaders = mapping.flatMap { it.loaders.keys }.toSet()
    val isSingleLoader = allLoaders.size == 1

    mcVersionToVersion.forEach { (mcVersion, modVersion) ->
        val config = mapping.find { it.mcVersion == mcVersion }
            ?: error("No loader config found for MC $mcVersion")

        val version = versionFormat(mcVersion, modVersion)

        config.loaders.forEach { (loaderName, loader) ->
            library(
                if (isSingleLoader) "${id}_${mcVersion}"
                else "${id}_${mcVersion}_$loaderName",
                "maven.modrinth",
                loader.slugTransformer(slug)
            ).version(loader.versionTransformer(version, loaderName))
        }
    }
}

dependencyResolutionManagement.versionCatalogs.create("catalog") {
    val mixinextras = "0.5.0-beta.2"
    library("mixinextras-forge", "io.github.llamalad7", "mixinextras-forge").version(mixinextras)
    library("mixinextras-fabric", "io.github.llamalad7", "mixinextras-fabric").version(mixinextras)

    modrinth(
        id = "surveyor",
        mcVersionToVersion = mapOf(
            "1.20" to "0.6.26",
            "1.21" to "0.6.26"
        ),
        versionFormat = VersionFormats.versionPlusMc,
        mapping = listOf(
            LoaderMapping(
                mcVersion = "1.21", loaders = mapOf(
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            ),
            LoaderMapping(
                mcVersion = "1.20", loaders = mapOf(
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            )
        )
    )

    modrinth(
        id = "surveystones",
        mcVersionToVersion = mapOf(
            "1.20" to "1.3.1",
            "1.21" to "1.3.1"
        ),
        versionFormat = VersionFormats.versionPlusMc,
        mapping = listOf(
            LoaderMapping(
                mcVersion = "1.21", loaders = mapOf(
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            ),
            LoaderMapping(
                mcVersion = "1.20", loaders = mapOf(
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            )
        )
    )

    modrinth(
        id = "moonlight",
        mcVersionToVersion = mapOf(
            "1.20" to "2.14.13",
            "1.21" to "2.19.5"
        ),
        versionFormat = VersionFormats.mcDashVersion,
        mapping = listOf(
            LoaderMapping(
                mcVersion = "1.21", loaders = mapOf(
                    "neoforge" to LoaderConfig(VersionTransformers.versionDashLoader),
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            ),
            LoaderMapping(
                mcVersion = "1.20", loaders = mapOf(
                    "forge" to LoaderConfig(VersionTransformers.versionDashLoader),
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            )
        )
    )

    modrinth(
        id = "mapAtlases",
        slug = "map-atlases",
        mcVersionToVersion = mapOf(
            "1.20" to "6.0.16",
            "1.21" to "6.3.5"
        ),
        versionFormat = VersionFormats.mcDashVersion,
        mapping = listOf(
            LoaderMapping(
                mcVersion = "1.21", loaders = mapOf(
                    "neoforge" to LoaderConfig(
                        slugTransformer = { "$it-forge" },
                        VersionTransformers.loaderUnderlineVersion
                    ),
                    "fabric" to LoaderConfig(VersionTransformers.loaderUnderlineVersion)
                )
            ),
            LoaderMapping(
                mcVersion = "1.20", loaders = mapOf(
                    "forge" to LoaderConfig(
                        slugTransformer = { "$it-forge" },
                        VersionTransformers.versionDashLoader
                    ),
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            )
        )
    )

    modrinth(
        id = "supplementaries",
        mcVersionToVersion = mapOf(
            "1.20" to "3.1.36",
            "1.21" to "3.3.5"
        ),
        versionFormat = VersionFormats.mcDashVersion,
        mapping = listOf(
            LoaderMapping(
                mcVersion = "1.21", loaders = mapOf(
                    "neoforge" to LoaderConfig(VersionTransformers.loaderUnderlineVersion),
                    "fabric" to LoaderConfig(VersionTransformers.loaderUnderlineVersion)
                )
            ),
            LoaderMapping(
                mcVersion = "1.20", loaders = mapOf(
                    "forge" to LoaderConfig(VersionTransformers.versionDashLoader),
                    "fabric" to LoaderConfig(VersionTransformers.versionDashLoader)
                )
            )
        )
    )

    val cardinalComponentsApi120 = "5.2.3"
    library("cardinalComponentsApi-base-1.20", "dev.onyxstudios.cardinal-components-api", "cardinal-components-base")
        .version(cardinalComponentsApi120)
    library("cardinalComponentsApi-item-1.20", "dev.onyxstudios.cardinal-components-api", "cardinal-components-item")
        .version(cardinalComponentsApi120)

    val cardinalComponentsApi121 = "6.1.2"
    library("cardinalComponentsApi-base-1.21", "org.ladysnake.cardinal-components-api", "cardinal-components-base")
        .version(cardinalComponentsApi121)
    library("cardinalComponentsApi-item-1.21", "org.ladysnake.cardinal-components-api", "cardinal-components-item")
        .version(cardinalComponentsApi121)
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

val name: String by settings

rootProject.name = name