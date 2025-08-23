package io.github.dkmarkell.test

import io.github.dkmarkell.textresource.TextResource
import io.github.dkmarkell.textresource.test.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
class TextResourceResolveTest {
    @Test
    fun givenRaw_whenResolve_thenReturnsValue() {

        // Given
        val tr = TextResource.raw("Hi")

        // When
        val actual = TextResourceTest.resolveString(tr)

        // Then
        assertEquals("Hi", actual)
    }

    @Test
    fun givenSimpleString_whenResolveInEnglish_thenFormatsName() {
        // Given
        val tr = TextResource.simple(R.string.greeting, "Derek")

        // When
        val actual = TextResourceTest.resolveString(tr, Locale.US)

        // Then
        assertEquals("Hello, Derek", actual)
    }

    @Test
    fun givenSimpleString_whenResolveInFrench_thenFormatsName() {
        // Given
        val tr = TextResource.simple(R.string.greeting, "Derek")

        // When
        val actual = TextResourceTest.resolveString(tr, Locale.FRANCE)

        // Then
        assertEquals("Bonjour, Derek", actual)
    }

    @Test
    fun givenPlural_whenResolve_thenHandlesOneAndOther() {
        // Given
        val one = TextResource.plural(R.plurals.apples_count, 1, 1)
        val many = TextResource.plural(R.plurals.apples_count, 2, 2)

        // When
        val oneResult  = TextResourceTest.resolveString(one)
        val manyResult = TextResourceTest.resolveString(many)

        // Then
        assertEquals("1 apple", oneResult)
        assertEquals("2 apples", manyResult)
    }

    @Test
    fun givenSamInitializer_whenResolve_thenUsesCustomLogic() {
        // Given
        val tr = TextResource { ctx ->
            ctx.getString(R.string.greeting, "World")
        }

        // When
        val actual = TextResourceTest.resolveString(tr)

        // Then
        assertEquals("Hello, World", actual)
    }
}