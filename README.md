# TextResource

[![Maven Central – core](https://img.shields.io/maven-central/v/io.github.dkmarkell/textresource-core?label=textresource-core)](https://central.sonatype.com/artifact/io.github.dkmarkell/textresource-core)
[![Maven Central – compose](https://img.shields.io/maven-central/v/io.github.dkmarkell/textresource-compose?label=textresource-compose)](https://central.sonatype.com/artifact/io.github.dkmarkell/textresource-compose)
[![Maven Central – test](https://img.shields.io/maven-central/v/io.github.dkmarkell/textresource-test?label=textresource-test)](https://central.sonatype.com/artifact/io.github.dkmarkell.textresource/textresource-test)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![minSdk](https://img.shields.io/badge/minSdk-21-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.25-blue)

A small Android library for representing and resolving text at the right time and place — without scattering string resolution logic across your UI.

## Installation

Add the dependencies to your `build.gradle`:
```kotlin
dependencies {
    implementation("io.github.dkmarkell.textresource:core:<version>")
    
    // If you use Compose:
    implementation("io.github.dkmarkell.textresource:compose:<version>")
}
```

### Testing (Robolectric)

```kotlin
// build.gradle.kts (module where your tests run)
dependencies {
  testImplementation("io.github.dkmarkell.textresource:textresource-test:<version>")
  testImplementation("org.robolectric:robolectric:<version>")
}

android {
  testOptions { unitTests.isIncludeAndroidResources = true }
}
```

#### Robolectric SDK cap (e.g., targetSdk = 36)

If your app targets a newer SDK than Robolectric supports, set the test SDK so unit tests run:

**Per test class**
```kotlin
@RunWith(org.robolectric.RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk = [35])
class MyTest { /* ... */ }
```

## Quick Start

```kotlin
// ViewModel
val title = TextResource.simple(R.string.greeting, userName)

// Compose
Text(title.resolveString())

// Views
textView.text = title.resolveString(context)
```

Check out the [Sample app](./sample) for a complete demo.

## Why?

In a clean architecture, your ViewModel (or presenter) should decide what text is displayed, but not actually need to hold a Context to do it. With Android’s resource system, resolving strings usually requires a Context — which is either unavailable or awkward to inject.

**TextResource** solves this by:

- Deferring resolution — store the definition of a string (e.g., resource ID + arguments) until it’s actually displayed.
- Keeping formatting and pluralization logic out of presentation components — no need for Composables or Views to assemble text from parts.
- Maintaining proper localization — the string is always resolved using the current configuration (locale, font scale, etc.).
- Supporting both Jetpack Compose and View-based UIs.

**TextResource** lets you keep string construction logic in your ViewModel (or other non-UI code) and resolve it only when rendering the UI.

Benefits:
- Keep UI code clean and focused on layout.
- Centralize localization, formatting, and pluralization.

2 common usecases **TextResource** solves

### Example 1: Exposing data to build strings
Without **TextResource**, you might pass raw data (like a name or count) up to the UI just so it can build a string:
```kotlin
// Without TextResource
// ViewModel exposes raw fields
val userName = "Derek"
val messageCount = 3

// UI has to know how to build the string
textView.text = context.getString(R.string.greeting, userName, messageCount)
```
With **TextResource**, you can pass the ready-to-resolve object instead:
```kotlin
// With TextResource
// ViewModel exposes the final representation
val greeting = TextResource.simple(R.string.greeting, "Derek", 3)

// UI just resolves it when needed
textView.text = greeting.resolveString(context)
```

### Example 2: Holding a Context in a ViewModel
A common anti-pattern is to hold a [Context] inside a ViewModel to build strings:
```kotlin
// Without TextResource
class MyViewModel(private val context: Context) : ViewModel() {
    val greeting = context.getString(R.string.greeting, userName)
}
```
With **TextResource**, you just hold a `TextResource`:
```kotlin
// With TextResource
class MyViewModel : ViewModel() {
    val greeting = TextResource.simple(R.string.greeting, "Derek", 3)
}

// Resolved later in the UI layer
textView.text = greeting.resolveString(context)
```
The UI (Activity/Fragment/Composable) provides the context at render time when resolving the string.

## Usage

### Creating TextResource
```kotlin
// Raw string
val raw = TextResource.raw("Hello World")

// From string resource
val simple = TextResource.simple(R.string.hello_user, "John")

// From plural resource
val plural = TextResource.plural(R.plurals.apples_count, count, count)

// Functional interface initializer (SAM)
val custom = TextResource { context ->
    val dayOfWeek = getDayOfWeek()
    context.getString(R.string.today, dayOfWeek)
}
```

### Resolving TextResource
```kotlin
// From a View based UI
val stringValue = textResource.resolveString(context)

// From Compose
val stringValue = textResource.resolveString()
```

### Remember helper in Compose
```kotlin
val welcome = rememberTextResource(key1 = username) {
    TextResource.simple(R.string.greeting_name, username)
}
Text(welcome.resolveString())
```

### ViewModel example
```kotlin
class HomeViewModel : ViewModel() {
    private val _title = MutableStateFlow(TextResource.raw(""))
    val title: StateFlow<TextResource> = _title

    private val _user = MutableStateFlow("you")
    val user: StateFlow<String> = _user

    private val _time = MutableStateFlow(
        TextResource { context ->
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val t = sdf.format(Date())
            context.getString(R.string.time, t)
        }
    )
    val time: StateFlow<TextResource> = _time

    fun onUnreadCountChanged(count: Int) {
        _title.value = TextResource.plural(R.plurals.unread_messages, count, count)
    }
}
```

### Compose example
```kotlin
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val time by vm.time.collectAsStateWithLifecycle()
    val title by vm.title.collectAsStateWithLifecycle()
    val user by vm.user.collectAsStateWithLifecycle()
    val welcome = rememberTextResource(key1 = user) {
        TextResource.simple(R.string.greeting_name, user)
    }
    HomeScreen(
        welcomeMessage = welcome.resolveString(),
        title = title.resolveString(),
        time = time.resolveString(),
        onRefresh = {
            vm.onUnreadCountChanged((1..9).random())
        }
    )
}

@Composable
private fun HomeScreen(
    welcomeMessage: String,
    title: String,
    time: String,
    onRefresh: () -> Unit = {}
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = welcomeMessage, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Text(text = title, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRefresh) {
                Text("Refresh")
            }
            Spacer(Modifier.height(16.dp))
            Text(text = time, style = MaterialTheme.typography.labelMedium)
        }
    }
}
```

## Testing with TextResourceTest

`textresource-test` provides a tiny helper that resolves `TextResource` in **local unit tests** (Robolectric), so your specs don’t need to wire a `Context` each time.

### Examples
```kotlin
// Basic
val tr = TextResource.simple(R.string.greeting, "Derek")
assertEquals("Hello, Derek", TextResourceTest.resolve(tr))

// Force locale
val tr = TextResource.simple(R.string.greeting, "Derek")
assertEquals("Bonjour, Derek", TextResourceTest.resolve(tr, Locale.FRANCE))

// Plurals
val apples = TextResource.plural(R.plurals.apples_count, 2, 2)
assertEquals("2 apples", TextResourceTest.resolve(apples))
```

## API Overview

### Constructing
**Core**
- **Factories (value-based)**
    - `TextResource.raw(value: String)`
    - `TextResource.simple(@StringRes resId: Int, vararg args: Any)`
    - `TextResource.plural(@PluralsRes resId: Int, quantity: Int, vararg args: Any)`
- **SAM initializer (functional interface)**
    - `TextResource { context -> /* resolve to a String using context */ }`

### Resolving
**Core**
```kotlin
fun TextResource.resolveString(context: Context): String
```

**Compose**
```kotlin
@Composable
fun TextResource.resolveString(): String
```

### Helpers
**Compose**
```kotlin
@Composable
fun rememberTextResource(factory: () -> TextResource): TextResource
@Composable
fun rememberTextResource(key1: Any?, factory: () -> TextResource): TextResource
@Composable
fun rememberTextResource(vararg keys: Any?, factory: () -> TextResource): TextResource
```

### Testing
**Test**
```kotlin
object TextResourceTest {
  @JvmStatic
  fun resolve(tr: TextResource, locale: Locale = Locale.US): String
}
```

### Equality semantics (important)
- Factory-created instances compare by **value** (same inputs → `==` is `true`)
- SAM-created instances compare by **reference** (each lambda is a new object)

## FAQ

**Q: Should I use the factory functions or the functional interface (SAM) initializer?**  
A: Use the factory functions (`raw`, `simple`, `plural`) in most cases. These return **value-based objects** that:
- Compare equal when constructed with the same inputs (`==` works as expected).
- Work well in collections (`List`, `Set`, `Map`).
- Are easier to test and reason about.

The SAM initializer (`TextResource { ... }`) creates an anonymous object. Each call produces a new instance, so:
- Equality is by reference only (two identical SAMs are *not* equal).
- Collections treat them as different objects, even if they resolve to the same text.
- Use SAMs when you need dynamic/custom resolution logic.

```kotlin
val a = TextResource.simple(R.string.greeting, "Derek")
val b = TextResource.simple(R.string.greeting, "Derek")
println(a == b) // true -> value-based

val x = TextResource { "Hello, Derek" }
val y = TextResource { "Hello, Derek" }
println(x == y) // false -> reference-based
```

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
