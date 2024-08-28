package cloud.rout.plugins

import cloud.rout.data.CountData
import cloud.rout.data.CountPost
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.webjars.*
import io.ktor.server.websocket.*
import io.ktor.util.Identity.decode
import io.ktor.util.Identity.encode
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.time.Duration

var latest = CountData(0)


fun Application.configureRouting() {
    install(Webjars) {
        path = "/webjars" //defaults to /webjars
    }
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        masking = true

    }

    routing {
        staticResources("/", "/dist")
    }
}
