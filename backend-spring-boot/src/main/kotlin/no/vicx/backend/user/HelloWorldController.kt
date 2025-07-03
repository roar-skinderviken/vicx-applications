package no.vicx.backend.user

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/hello"])
class HelloWorldController {
    @GetMapping
    fun hello(
        @RequestHeader("X-Forwarded-For") forwardedForHeader: String,
    ) = "Hello World! $forwardedForHeader"
}
