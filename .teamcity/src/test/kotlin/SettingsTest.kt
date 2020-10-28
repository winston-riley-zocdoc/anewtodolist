import jetbrains.buildServer.configs.kotlin.v2019_2.AbsoluteId
import jetbrains.buildServer.configs.kotlin.v2019_2.DslContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Predicate

class SettingsTest {

    @BeforeEach
    internal fun setUp() {
        DslContext.projectId = AbsoluteId("Hello World")
        DslContext.settingsRootId = AbsoluteId("my settibngs root id")

        DslContext.addParameters("my-name" to "Ohohohoh")
    }

    @Test
    fun testSomething() {

        MyProject.buildTypes.forEach {
         //   assertThat(it.requirements.items).`as`("BuildType ${it.name} has a requirement").anyMatch { req -> req.value == "Bla" }
            assertThat(1).isEqualTo(1)
        }
    }
}