package no.vicx.backend.messages

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class MessagesController {

    @GetMapping("/messages")
    fun getMessages(): Array<String> = arrayOf("Message 1", "Message 2", "Message 3")
}
