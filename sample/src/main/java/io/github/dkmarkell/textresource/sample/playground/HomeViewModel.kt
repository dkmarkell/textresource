package io.github.dkmarkell.textresource.sample.playground

import androidx.lifecycle.ViewModel
import io.github.dkmarkell.textresource.TextResource
import io.github.dkmarkell.textresource.sample.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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