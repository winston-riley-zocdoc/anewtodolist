import jetbrains.buildServer.configs.kotlin.v10.toExtId
import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.freeDiskSpace
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    val buildSteps = sequential {
        // build
        buildType(Maven("Build", "clean compile"))

        // test
        parallel {
            buildType(
                Maven(
                    "Fast Tests",
                    "clean test",
                    "-Dmaven.test.failure.ignore=true -Dtest=*.unit.*Test"
                )
            )
            buildType(
                Maven(
                    "Slow Test",
                    "clean test",
                    "-Dmaven.test.failure.ignore=true -Dtest=*.integration.*Test"
                )
            )
        }

        // package
        buildType(
            Maven(
                "Package",
                "clean package",
                "-Dmaven.test.failure.ignore=true -DskipTests"
            )
        )
    }.buildTypes()

    buildSteps.forEach {this.buildType(it)}

    buildSteps.last().triggers {
        vcs {}
    }
}

class Maven(name: String, goals: String, runnerArgs: String? = null) : BuildType({
    id(name.toExtId())
    this.name = name

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            this.goals = goals
            this.runnerArgs = runnerArgs
            jdkHome = "/Library/Java/JavaVirtualMachines/amazon-corretto-11.jdk/Contents/Home"
        }
    }

    features {
        freeDiskSpace {
            requiredSpace = "5gb"
            failBuild = true
        }
    }
})