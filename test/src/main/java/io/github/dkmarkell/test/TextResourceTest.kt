package io.github.dkmarkell.test

import io.github.dkmarkell.textresource.TextResource
import java.util.Locale

/**
 * Test utilities for resolving a [TextResource] to a [String] in **local unit tests**.
 *
 * Centralizes the Robolectric boilerplate (application + localized configuration context),
 * so your specs can assert on final strings without wiring up `Context` for every test.
 *
 * Use cases:
 * - Verify string resources and formatting args
 * - Verify plurals across quantities
 * - Verify localization by forcing a specific [java.util.Locale]
 *
 * ### Examples
 * ```kotlin
 * // Simple string with args
 * val tr = TextResource.simple(R.string.greeting, "Derek")
 * assertEquals("Hello, Derek", TextResourceTest.resolve(tr))
 *
 * // Force a different locale
 * val tr = TextResource.simple(R.string.greeting, "Derek")
 * assertEquals("Bonjour, Derek",
 *     TextResourceTest.resolve(tr, locale = Locale.FRANCE))
 *
 * // Plurals
 * val apples = TextResource.plural(R.plurals.apples_count, 2, 2)
 * assertEquals("2 apples", TextResourceTest.resolve(apples))
 * ```
 *
 * ### Requirements
 * - Runs under **Robolectric** (JVM unit tests in `src/test`), not `androidTest`.
 * - Ensure `testOptions.unitTests.isIncludeAndroidResources = true` in your module.
 * - Reference your module’s generated `R` (e.g., `import my.pkg.test.R`), or alias it if multiple modules define `R`.
 *
 * ### Notes
 * - This helper is for tests only; it is not required at runtime.
 * - For Compose code in production, prefer the `:compose` extension `TextResource.resolveString()` inside composition.
 */
public object TextResourceTest {
    /**
     * Resolves the given [TextResource] to a [String] using a localized Robolectric [android.content.Context].
     *
     * Internally:
     * - Obtains the application context from Robolectric
     * - Applies the requested [locale] to a new configuration context
     * - Delegates to [TextResource.resolveString] with that context
     *
     * @param textResource The [TextResource] under test (raw, simple, or plural).
     * @param locale The [java.util.Locale] to apply for resource lookup. Defaults to [java.util.Locale.US].
     * @return The resolved string for the given configuration.
     * @throws android.content.res.Resources.NotFoundException if the resource ID is invalid for this module.
     *
     * ### Example
     * ```kotlin
     * val tr = TextResource.simple(R.string.greeting, "Derek")
     * val result = TextResourceTest.resolve(tr, Locale.FRANCE)
     * assertEquals("Bonjour, Derek", result)
     * ```
     */
    @JvmStatic
    public fun resolveString(textResource: TextResource, locale: Locale = Locale.US): String {
        val app = org.robolectric.RuntimeEnvironment.getApplication()
        val conf = app.resources.configuration.apply { setLocale(locale) }
        val localized = app.createConfigurationContext(conf)
        return textResource.resolveString(localized)
    }
}