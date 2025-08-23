package io.github.dkmarkell.textresource.sample

import io.github.dkmarkell.test.TextResourceTest
import io.github.dkmarkell.textresource.sample.playground.HomeViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.Locale
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SampleTest {

    @Test
    fun givenUnreadCount_whenResolveViaHelper_thenMatchesResources() {
        // Given
        val vm = HomeViewModel()
        vm.onUnreadCountChanged(2) // sets title to TextResource.plural(..., 2, 2)

        // When
        val tr = vm.title.value
        val actual = TextResourceTest.resolveString(tr, Locale.US)

        // Then (compare with Android's own plural resolution)
        val expected = RuntimeEnvironment.getApplication()
            .resources.getQuantityString(R.plurals.unread_messages, 2, 2)

        assertEquals(expected, actual)
    }
}