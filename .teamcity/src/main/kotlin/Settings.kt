import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs


object MyProject: Project({
    val bts = sequential {
        buildType(Maven(DslContext.getParameter("my-name"), "clean compile"))
        parallel {
            buildType(Maven("Slow Test", "clean test", "-Dtest=*.integration.*Test"))
            buildType(Maven("Fast Test", "clean test", "-Dtest=*.unit.*Test"))
        }
        buildType(Maven("Package", "clean package", "-DskipTests"))
    }.buildTypes()

    bts.forEach { buildType(it) }
    bts.last().triggers {
        vcs {}
    }
})

class Maven(name: String, goals: String, runnerArgs: String? = null): BuildType({
    this.name = name

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            this.goals = goals;
            this.runnerArgs = runnerArgs;
        }
    }
})
