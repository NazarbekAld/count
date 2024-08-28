package cloud.rout

import cloud.rout.data.CountData
import cloud.rout.data.CountPost
import cloud.rout.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration

val messageResponseFlow = MutableSharedFlow<CountData>()
val sharedFlow = messageResponseFlow.asSharedFlow()

fun main() {
    embeddedServer(Netty, 6001) {
        install(WebSockets) {
            pingPeriod = Duration.ofMinutes(1)
        }
        routing {
            webSocket("count_flow") {
                send(Json.encodeToString(latest))

                val job = launch {
                    sharedFlow.collect {
                        send(Json.encodeToString(it))
                    }
                }

                runCatching {
                    incoming.consumeEach { frame ->
                        if (frame is Frame.Text) {
                            val message = frame.readText()
                            val op = Json.decodeFromString<CountPost>(message)
                            if (op.op == "add") {
                                latest++
                                messageResponseFlow.emit(latest)
                            }
                        }
                    }
                }.onFailure { exception ->
                    println("WebSocket exception: ${exception.localizedMessage}")
                }.also {
                    job.cancel()
                }

            }
        }
    }.start(wait = false)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    configureSecurity()
    configureHTTP()
    configureSerialization()
//    configureTemplating()
    configureRouting()
}
