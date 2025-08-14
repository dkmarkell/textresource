package io.github.dkmarkell.textresource

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TextResourceTest {

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun givenRaw_whenResolve_thenReturnsValue() {
        // Given
        val tr = TextResource.raw("Howdy")

        // When
        val result = tr.resolveString(ctx)

        // Then
        assertEquals("Howdy", result)
    }

    @Test
    fun givenSimpleWithArgs_whenResolve_thenFormatsCorrectly() {
        // Given (R.string.hello_user = "Hello %1$s")
        val tr = TextResource.simple(R.string.hello_user, "Sam")

        // When
        val result = tr.resolveString(ctx)

        // Then
        assertEquals("Hello Sam", result)
    }

    @Test
    fun givenPlural_whenResolve_thenUsesCorrectQuantityForm() {
        // Given
        val one = TextResource.plural(R.plurals.apples_count, 1, 1)
        val many = TextResource.plural(R.plurals.apples_count, 5, 5)

        // When
        val oneResult = one.resolveString(ctx)
        val manyResult = many.resolveString(ctx)

        // Then
        assertEquals("1 apple", oneResult)
        assertEquals("5 apples", manyResult)
    }

    @Test
    fun givenCustomSAM_whenResolve_thenCanUseContextResources() {
        // Given
        val tr = TextResource { c ->
            c.getString(R.string.hello_user, "Dev")
        }

        // When
        val result = tr.resolveString(ctx)

        // Then
        assertEquals("Hello Dev", result)
    }
}
