package io.github.dkmarkell.textresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import io.github.dkmarkell.textresource.TextResource

/**
 * Creates and stores a [TextResource] for the lifetime of the current composition.
 *
 * The [factory] is invoked only once while this Composable is in the composition.
 * The same [TextResource] instance is returned on every recomposition.
 *
 * Use this when the text is static and does not depend on any changing state.
 *
 * Example:
 * ```
 * val title = rememberTextResource { TextResource.simple(R.string.app_title) }
 * Text(title.resolveString())
 * ```
 *
 * @param factory Lambda used to construct the [TextResource].
 */
@Composable
public fun rememberTextResource(
    factory: () -> TextResource
): TextResource = remember { factory() }

/**
 * Creates and stores a [TextResource] that is recreated when [key1] changes.
 *
 * The [factory] runs the first time this Composable enters the composition,
 * and again whenever [key1] is different from the previous composition.
 *
 * Use this when the text depends on a single piece of state.
 *
 * Example:
 * ```
 * val greeting = rememberTextResource(userName) {
 *     TextResource.simple(R.string.greeting, userName)
 * }
 * Text(greeting.resolveString())
 * ```
 *
 * @param key1 Value to watch for changes; triggers recreation if different.
 * @param factory Lambda used to construct the [TextResource].
 */
@Composable
public fun rememberTextResource(
    key1: Any?,
    factory: () -> TextResource
): TextResource = remember(key1) { factory() }

/**
 * Creates and stores a [TextResource] that is recreated when any of [keys] change.
 *
 * The [factory] runs the first time this Composable enters the composition,
 * and again whenever one or more keys are different from the previous composition.
 *
 * Use this when the text depends on multiple pieces of state.
 *
 * Example:
 * ```
 * val message = rememberTextResource(userName, unreadCount) {
 *     TextResource.plural(R.plurals.unread_messages, unreadCount, userName, unreadCount)
 * }
 * Text(message.resolveString())
 * ```
 *
 * @param keys Values to watch for changes; triggers recreation if any differ.
 * @param factory Lambda used to construct the [TextResource].
 */
@Composable
public fun rememberTextResource(
    vararg keys: Any?,
    factory: () -> TextResource
): TextResource = remember(*keys) { factory() }

