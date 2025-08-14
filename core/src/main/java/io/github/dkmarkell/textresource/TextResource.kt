package io.github.dkmarkell.textresource

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

/**
 * Represents a piece of text that can be resolved into a [String] at runtime, given an
 * Android [Context].
 *
 * Pass instances around (raw strings, string resources, plurals) without resolving immediately.
 * This enables:
 * - Proper localization via resource lookup
 * - Resolve at render time
 * - Easy testing (you can stub resolution)
 *
 * Create instances using the [Companion] factory functions, or use the functional interface
 * initializer directly:
 * ```
 * val custom = TextResource { context ->
 *     "Hello, ${context.getString(R.string.your_apps_string)}" // from your app’s R
 * }
 * ```
 *
 * ### Example (factory)
 * ```
 * val greeting = TextResource.simple(R.string.greeting, userName)
 * ```
 */
public fun interface TextResource {
    /**
     * Resolves this [TextResource] into a [String] using the given [context].
     *
     * This is the core operation: it turns a raw value, string resource, or plurals resource
     * into displayable text for the current configuration.
     *
     * - Resolution is **context-dependent** so localization and configuration
     *   (e.g., locale, font scale) are handled correctly.
     * - In Compose, prefer the extension in `:compose`
     *   (`TextResource.resolveString()`) so configuration changes trigger
     *   recomposition and re-resolution automatically.
     *
     * @param context Android [Context] used to look up resources.
     * @return The resolved string for the current configuration.
     */
    public fun resolveString(context: Context): String

    public companion object {
        /**
         * Creates a [TextResource] from a raw, non-localized [String].
         *
         * @param value The exact string to return when [resolveString] is called.
         */
        @JvmStatic
        public fun raw(value: String): TextResource = TextResource { value }

        /**
         * Creates a [TextResource] from a string resource.
         *
         * @param resId String resource ID from your app/module.
         * @param args  Optional formatting arguments passed to [Context.getString].
         *
         * ### Examples
         * ```
         * val title = TextResource.simple(R.string.app_title)
         * val greeting = TextResource.simple(R.string.greeting, userName)
         * ```
         */
        @JvmStatic
        public fun simple(@StringRes resId: Int, vararg args: Any): TextResource =
            TextResource { context ->
                context.getString(resId, *args)
            }

        /**
         * Creates a [TextResource] from a plurals resource.
         *
         * @param resId    Plurals resource ID from your app/module.
         * @param quantity The count used to select the plural form.
         * @param args     Optional formatting arguments passed to
         *                 [android.content.res.Resources.getQuantityString].
         *
         * ### Example
         * ```
         * val apples = TextResource.plural(R.plurals.apple_count, count, count)
         * ```
         */
        @JvmStatic
        public fun plural(@PluralsRes resId: Int, quantity: Int, vararg args: Any): TextResource =
            TextResource { context ->
                context.resources.getQuantityString(resId, quantity, *args)
            }
    }
}