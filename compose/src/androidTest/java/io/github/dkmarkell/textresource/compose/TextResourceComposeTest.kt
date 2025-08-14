package io.github.dkmarkell.textresource.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.Locales
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.intl.LocaleList
import androidx.test.filters.SdkSuppress
import io.github.dkmarkell.textresource.TextResource
import org.junit.Rule
import org.junit.Test
import io.github.dkmarkell.textresource.compose.test.R as testR

/**
 * On 33+ I'm running into issues where the test app is requesting notification
 * permissions, which causes the tests to hang. I'm not sure why this permission
 * is being requested and I'm unable to stop it from being requested and having
 * trouble granting it. Workaround is to run tests on 32 or lower.
 */
@SdkSuppress(maxSdkVersion = 32)
class TextResourceComposeTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun givenSimpleTextResource_whenResolveInCompose_thenTextShowsString() {
        // Given
        val tr = TextResource.simple(testR.string.hello)

        // When
        rule.setContent {
            Text(tr.resolveString())
        }

        // Then
        rule.onNodeWithText("Hello").assertExists()
    }

    @Test
    fun givenLocaleToggle_whenDeviceLocalesOverrideChanges_thenTextUpdates() {
        // Given
        val tr = TextResource.simple(testR.string.hello) // values: "Hello", values-fr: "Bonjour"
        lateinit var setFrench: () -> Unit

        // When (single setContent)
        rule.setContent {
            var french by remember { mutableStateOf(false) }
            setFrench = { french = true }

            val localesOverride = if (french) {
                DeviceConfigurationOverride.Locales(LocaleList("fr"))
            } else {
                DeviceConfigurationOverride.Locales(LocaleList("en"))
            }

            DeviceConfigurationOverride(localesOverride) {
                Text(tr.resolveString())
            }
        }

        // Then (initial EN)
        rule.onNodeWithText("Hello").assertExists()

        // When (flip to FR → triggers recomposition)
        rule.runOnIdle { setFrench() }

        // Then (updated FR)
        rule.onNodeWithText("Bonjour").assertExists()
    }

    @Test
    fun givenRememberTextResource_whenNoKeysChange_thenSameInstanceTextPersistsAcrossRecompositions() {
        // Given
        var factoryCalls = 0
        lateinit var trigger: () -> Unit

        // When
        rule.setContent {
            var tick by remember { mutableStateOf(false) }
            trigger = { tick = true }

            val tr = rememberTextResource {
                factoryCalls += 1
                TextResource.raw("X$factoryCalls") // encode call count into value
            }

            // Render current value so we can assert it via semantics
            Text(tr.resolveString())

            // Trigger a single recomposition without changing keys
            LaunchedEffect(Unit) { tick = true }
        }

        // Then (factory must have run only once and text stays "X1")
        rule.onNodeWithText("X1").assertExists()
        rule.runOnIdle { assert(factoryCalls == 1) }
    }

    @Test
    fun givenRememberTextResourceWithKey_whenKeyChanges_thenNewInstanceTextAppears() {
        // Given
        var factoryCalls = 0
        lateinit var setUser: (String) -> Unit

        // When
        rule.setContent {
            var user by remember { mutableStateOf("Alice") }
            setUser = { user = it }

            val tr = rememberTextResource(user) {
                factoryCalls += 1
                TextResource.raw("call=$factoryCalls user=$user")
            }

            Text(tr.resolveString())
        }

        // Then (initial text reflects first call + key: Alice)
        rule.onNodeWithText("call=1 user=Alice").assertExists()

        // When (change key → forces recreation)
        rule.runOnIdle { setUser("Bob") }

        // Then (updated text reflects second call + new key)
        rule.onNodeWithText("call=2 user=Bob").assertExists()
        rule.runOnIdle { assert(factoryCalls == 2) }
    }
}


