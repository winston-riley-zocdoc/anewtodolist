import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import jetbrains.buildServer.configs.kotlin.v2019_2.Requirement
import jetbrains.buildServer.configs.kotlin.v2019_2.RequirementType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SettingsTest {

    @BeforeEach
    internal fun setUp() {
        DslContext.projectId = AbsoluteId("My Project")
    }

    @Test
    fun testForJava11Requirement() {
        val project = MyProject

        project.buildTypes.forEach {
            bt -> assertThat(bt.requirements.items)
                .`as`("${bt.id} needs a java 11 requirement")
                .contains(java11Requirement())}
    }

    private fun java11Requirement(): Requirement {
        return Requirement(RequirementType.NO_LESS_THAN_VER,
                "teamcity.agent.jvm.version", "11")
    }
}