package io.github.dkmarkell.textresource

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
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

    @Test
    fun givenTwoRawWithSameValue_whenCompared_thenAreEqual() {
        // Given
        val a = TextResource.raw("hi")
        val b = TextResource.raw("hi")

        // When / Then
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun givenTwoRawWithDifferentValue_whenCompared_thenAreNotEqual() {
        // Given
        val a = TextResource.raw("hi")
        val b = TextResource.raw("bye")

        // When / Then
        assertNotEquals(a, b)
    }

    @Test
    fun givenTwoSimpleWithSameResAndArgs_whenCompared_thenAreEqual() {
        // Given
        val a = TextResource.simple(R.string.hello_friends, "Derek", "Jim")
        val b = TextResource.simple(R.string.hello_friends, "Derek", "Jim")

        // When / Then
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun givenTwoSimpleWithDifferentArgs_whenCompared_thenAreNotEqual() {
        // Given
        val a = TextResource.simple(R.string.hello_friends, "Derek", "Jim")
        val b = TextResource.simple(R.string.hello_friends, "Derek", "Bob")

        // When / Then
        assertNotEquals(a, b)
    }

    @Test
    fun givenTwoPluralWithSameValues_whenCompared_thenAreEqual() {
        // Given
        val a = TextResource.plural(R.plurals.apples_count, 2, 2)
        val b = TextResource.plural(R.plurals.apples_count, 2, 2)

        // When / Then
        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    @Test
    fun givenTwoPluralWithDifferentQuantity_whenCompared_thenAreNotEqual() {
        // Given
        val a = TextResource.plural(R.plurals.apples_count, 2, 2)
        val b = TextResource.plural(R.plurals.apples_count, 3, 3)

        // When / Then
        assertNotEquals(a, b)
    }

    @Test
    fun givenTwoSimpleWithDifferentArgOrder_whenCompared_thenAreNotEqual() {
        // Given
        val a = TextResource.simple(R.string.hello_friends, "A", "B")
        val b = TextResource.simple(R.string.hello_friends, "B", "A")

        // When / Then
        assertNotEquals(a, b)
    }

    @Test
    fun givenFactoriesAndSam_whenCompared_thenFactoriesEqualButSamNotEqual() {
        // Given
        val f1 = TextResource.simple(R.string.hello_user, "Derek")
        val f2 = TextResource.simple(R.string.hello_user, "Derek")

        val s1 = TextResource { "hi" }
        val s2 = TextResource { "hi" }

        // When / Then
        assertEquals(f1, f2)    // data-class equality
        assertNotEquals(s1, s2) // SAM reference equality
    }

    @Test
    fun givenFactoryInstances_whenUsedInList_thenBehaveByValue() {
        // Given
        val a = TextResource.simple(R.string.hello_user, "Derek")
        val b = TextResource.simple(R.string.hello_user, "Derek")

        // When
        val list = listOf(a)

        // Then
        assertTrue(list.contains(b)) // passes because equals() works by value
    }

    @Test
    fun givenVarargMutationAfterCreation_whenCompared_thenEqualityUnchanged() {
        // Given
        val arr = arrayOf<Any>("A")
        val tr = TextResource.simple(R.string.hello_user, *arr)

        // When
        arr[0] = "B" // mutate original array
        val expected = TextResource.simple(R.string.hello_user, "A")

        // Then
        assertEquals(expected, tr) // still equal, because args were copied into a List
    }

    @Test
    fun givenList_whenContainsSamWithSameLogic_thenDoesNotContain() {
        // Given
        val sam1 = TextResource { "Hello, Derek" } // SAM (anonymous class)
        val sam2 = TextResource { "Hello, Derek" } // another SAM instance

        val list = listOf(sam1)

        // When / Then
        // SAMs compare by reference, so only the *same* instance is contained.
        assertTrue(list.contains(sam1))
        assertFalse(list.contains(sam2))
    }
}
