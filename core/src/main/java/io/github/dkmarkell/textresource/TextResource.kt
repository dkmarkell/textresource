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
 *
 * ### Equality & identity
 * Instances created via the **factory functions** (`raw`, `simple`, `plural`) are backed by
 * private data classes and therefore have **value-based equality and stable hash codes**.
 * This makes them safe to use as keys (e.g., in `remember(key = …)`, `distinctUntilChanged()`,
 * `Set`/`Map`, DiffUtil, etc.).
 *
 * Instances created via the **SAM initializer**:
 * ```
 * val custom = TextResource { ctx -> "Hello, ${ctx.getString(R.string.app_name)}" }
 * ```
 * are anonymous implementations with **reference equality** (two instances are equal only if
 * they are the same object). Prefer the factory functions whenever you plan to compare,
 * deduplicate, or cache `TextResource` instances.
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
         * - Returns a **value-equal, comparable** instance (backed by a private data class).
         *   Prefer this over the SAM initializer if you need equality or hashing semantics.
         *
         * @param text The exact string to return when [resolveString] is called.
         * @return A [TextResource] that resolves to the given raw string.
         */
        @JvmStatic
        public fun raw(text: String): TextResource {
            return Raw(text = text)
        }

        /**
         * Creates a [TextResource] from a string resource.
         *
         * - Returns a **value-equal, comparable** instance (backed by a private data class).
         *   Prefer this over the SAM initializer if you need equality or hashing semantics.
         *
         * @param resId String resource ID from your app/module.
         * @param args  Optional formatting arguments passed to [Context.getString].
         * @return A [TextResource] that resolves via [Context.getString] for the given resource and arguments.
         *
         * ### Examples
         * ```
         * val title = TextResource.simple(R.string.app_title)
         * val greeting = TextResource.simple(R.string.greeting, userName)
         * ```
         */
        @JvmStatic
        public fun simple(@StringRes resId: Int, vararg args: Any): TextResource {
            return Simple(resId = resId, args = args.toList())
        }

        /**
         * Creates a [TextResource] from a plurals resource.
         *
         * - Returns a **value-equal, comparable** instance (backed by a private data class).
         *   Prefer this over the SAM initializer if you need equality or hashing semantics.
         *
         * @param resId    Plurals resource ID from your app/module.
         * @param quantity The count used to select the plural form.
         * @param args     Optional formatting arguments passed to
         *                 [android.content.res.Resources.getQuantityString].
         * @return A [TextResource] that resolves via
         *         [android.content.res.Resources.getQuantityString] for the given resource, quantity, and arguments.
         *
         * ### Example
         * ```
         * val apples = TextResource.plural(R.plurals.apple_count, count, count)
         * ```
         */
        @JvmStatic
        public fun plural(@PluralsRes resId: Int, quantity: Int, vararg args: Any): TextResource {
            return Plural(resId = resId, quantity = quantity, args = args.toList())
        }
    }
}

/**
 * Private value type backing [TextResource.raw].
 *
 * - **Immutability:** stores a snapshot `String`.
 * - **Equality:** value-based; two [Raw] instances are equal if their [text] is equal.
 * - **Why factory:** prefer constructing via [TextResource.raw] so callers get stable
 *   equality semantics; the SAM initializer would not provide that.
 *
 * @property text Exact string returned by [resolveString].
 */
private data class Raw(val text: String) : TextResource {
    override fun resolveString(context: Context): String {
        return text
    }
}


/**
 * Private value type backing [TextResource.simple].
 *
 * - **Immutability:** stores [resId] and an immutable [args] list (created at the factory).
 * - **Equality:** value-based across [resId] and [args]; safe for use in `remember` keys,
 *   `distinctUntilChanged`, and as map/set keys.
 * - **Resolution:** delegates to [Context.getString] with the provided arguments.
 * - **Why factory:** SAM implementations have reference equality only; this class gives
 *   structural equality for deduping and caching.
 *
 * @property resId String resource ID.
 * @property args  Formatting arguments captured as an immutable list.
 */
private data class Simple(@StringRes val resId: Int, val args: List<Any>) : TextResource {
    override fun resolveString(context: Context): String {
        return context.getString(resId, *args.toTypedArray())
    }
}

/**
 * Private value type backing [TextResource.plural].
 *
 * - **Immutability:** stores [resId], [quantity], and an immutable [args] list.
 * - **Equality:** value-based across [resId], [quantity], and [args]; safe for use in
 *   `remember` keys, `distinctUntilChanged`, and as map/set keys.
 * - **Resolution:** delegates to [android.content.res.Resources.getQuantityString].
 * - **Why factory:** SAM implementations compare by reference; this class enables correct
 *   structural equality for collections/deduplication.
 *
 * @property resId    Plurals resource ID.
 * @property quantity Quantity used to select the plural form.
 * @property args     Formatting arguments captured as an immutable list.
 */
private data class Plural(@PluralsRes val resId: Int, val quantity: Int, val args: List<Any>) :
    TextResource {
    override fun resolveString(context: Context): String {
        return context.resources.getQuantityString(resId, quantity, *args.toTypedArray())
    }
}

