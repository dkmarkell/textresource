package io.github.dkmarkell.textresource.sample.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.dkmarkell.textresource.TextResource
import io.github.dkmarkell.textresource.compose.rememberTextResource
import io.github.dkmarkell.textresource.compose.resolveString
import io.github.dkmarkell.textresource.sample.R

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

private class TextResourceSamples : PreviewParameterProvider<TextResource> {
    override val values = sequenceOf(
        TextResource.raw("Let's start"),
        TextResource.plural(R.plurals.unread_messages, 1, 1),
        TextResource.plural(R.plurals.unread_messages, 5, 5),
    )
}

@Preview
@Composable
private fun HomeScreenPreview_All(@PreviewParameter(TextResourceSamples::class) tr: TextResource) {
    MaterialTheme {
        Surface {
            HomeScreen(
                welcomeMessage = TextResource.simple(R.string.greeting_name, "you").resolveString(),
                title = tr.resolveString(),
                time = TextResource { "It is 12:00" }.resolveString()
            )
        }
    }

}