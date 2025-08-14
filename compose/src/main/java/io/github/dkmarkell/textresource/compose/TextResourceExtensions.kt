package io.github.dkmarkell.textresource.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import io.github.dkmarkell.textresource.TextResource


/**
 * Resolves this [TextResource] to a [String] **inside Compose**.
 *
 * This is a convenience for calling the core API’s [TextResource.resolveString] without manually
 * passing an Android `Context`.
 *
 * It uses the current composition’s [LocalContext].
 *
 * This variant also observes [LocalConfiguration], so when configuration
 * changes (e.g., **locale** or **font scale**) occur, the composable will recompose and the text
 * will be re-resolved with the updated context.
 *
 * ### Example
 * ```
 * @Composable
 * fun Greeting(title: TextResource) {
 *     androidx.compose.material3.Text(title.resolveString())
 * }
 * ```
 *
 * For non-Compose code (e.g., ViewModel, domain, tests), call the core API
 * `TextResource.resolveString(context)` and pass a `Context` explicitly.
 */
@Composable
@ReadOnlyComposable
public fun TextResource.resolveString(): String {
    LocalConfiguration.current // ensures recomposition on configuration change
    return resolveString(context = LocalContext.current)
}