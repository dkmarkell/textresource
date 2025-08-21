package io.github.dkmarkell.test

import io.github.dkmarkell.textresource.TextResource
import java.util.Locale

public object TextResourceTest {
    @JvmStatic
    public fun resolveString(textResource: TextResource, locale: Locale = Locale.US): String {
        val app = org.robolectric.RuntimeEnvironment.getApplication()
        val conf = app.resources.configuration.apply { setLocale(locale) }
        val localized = app.createConfigurationContext(conf)
        return textResource.resolveString(localized)
    }
}