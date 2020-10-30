import jetbrains.buildServer.configs.kotlin.v10.toExtId
import jetbrains.buildServer.configs.kotlin.v2019_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.sequential
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

object MyProject: Project({
    vcsRoot(MyGitVcsRoot)

    val bts = sequential {
        buildType(Maven("Test Build Script", "clean test", null, ".teamcity/pom.xml"))
        buildType(Maven("Build", "clean compile"))
        parallel {
            buildType(Maven("Fast Test", "clean test", "-Dmaven.test.failure.ignore=true -Dtest=*.unit.*Test"))
            buildType(Maven("Slow Test", "clean test", "-Dmaven.test.failure.ignore=true -Dtest=*.integration.*Test"))
        }
        buildType(Maven("Package", "clean package", "-DskipTests"))
    }.buildTypes()

    bts.forEach { buildType(it) }
    bts.last().triggers {
        vcs {

        }
    }
})

object MyGitVcsRoot: GitVcsRoot({
    name = DslContext.getParameter("gitVcsName")
    url = DslContext.getParameter("gitVcsUrl")
    branch = DslContext.getParameter("gitVcsBranch", "refs/heads/main")
})

class Maven(name: String, goals: String, runnerArgs: String? = null, pomLocation: String? = null) : BuildType({
    id(name.toExtId())
    this.name = name

    vcs {
        root(MyGitVcsRoot)
    }

    steps {
        maven {
            this.pomLocation = pomLocation
            this.goals = goals
            this.runnerArgs = runnerArgs
        }
    }

    requirements {
        noLessThanVer("teamcity.agent.jvm.version", "11")
    }
})
