package no.vicx.user

object ValidationUtils {
    val usernameRegex = Regex("^[a-zA-Z0-9_-]+$")
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*\$")
    val emailRegex = Regex("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")

    fun String.validateNameLen(): Boolean = this.length !in 4..255
    fun String.validatePasswordLen(): Boolean = this.length !in 8..255
}