package no.vicx.ktor.user

object ValidationUtils {
    private val usernameRegex = Regex("^[a-zA-Z0-9_-]+$")
    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*\$")
    private val emailRegex = Regex("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
    private val leadingOrTrailingBlanksRegex = Regex("^\\S.*\\S\$|^\\S\$")

    fun String.validateUsername(validationErrors: MutableList<String>) {
        when {
            isBlank() -> validationErrors.add("Username cannot be blank")
            length !in 4..255 -> validationErrors.add("Username must be between 4 and 255 characters")
            !usernameRegex.matches(this) ->
                validationErrors.add("Username can only contain letters, numbers, hyphens, and underscores")
        }
    }

    fun String.validateEmail(
        validationErrors: MutableList<String>,
        checkForBlank: Boolean = true
    ) {
        when {
            checkForBlank && isBlank() -> validationErrors.add("Email cannot be blank")
            !emailRegex.matches(this) -> validationErrors.add("Email format is invalid")
        }
    }

    fun String.validateName(validationErrors: MutableList<String>) {
        when {
            isBlank() -> validationErrors.add("Name cannot be blank")
            length !in 4..255 -> validationErrors.add("Name must be between 4 and 255 characters")
            !leadingOrTrailingBlanksRegex.matches(this) -> validationErrors.add("Name cannot have leading or trailing blanks")
        }
    }

    fun String.validatePassword(validationErrors: MutableList<String>) {
        when {
            isBlank() -> validationErrors.add("Password cannot be blank")
            length !in 8..255 -> validationErrors.add("Password must be between 8 and 255 characters")
            !passwordRegex.matches(this) ->
                validationErrors.add("Password must contain at least one lowercase letter, one uppercase letter, and one digit")
        }
    }
}