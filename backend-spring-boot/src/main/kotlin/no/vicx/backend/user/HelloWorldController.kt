package no.vicx.backend.user

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/hello"])
class HelloWorldController {
    @GetMapping
    fun hello(request: HttpServletRequest): String {
        val clientIp = request.remoteAddr
        return "Hello World! $clientIp"
    }
}
