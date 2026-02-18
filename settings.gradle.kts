pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }   // MPAndroidChart
    }

    versionCatalogs {
        create("libs") {
            from(files("libs.versions.toml"))
        }
    }
}

rootProject.name = "FinCalc"
include(":app")
