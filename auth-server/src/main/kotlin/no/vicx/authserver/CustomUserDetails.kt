package no.vicx.authserver

import no.vicx.database.user.VicxUser
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class CustomUserDetails(
    username: String,
    password: String,
    val name: String,
    val email: String,
    val hasImage: Boolean
) : User(username, password, GRANTED_AUTHORITIES) {

    /**
     * Convenient constructor for creating a CustomUserDetails instance from a VicxUser.
     *
     * @param user VicxUser from the database
     */
    constructor(user: VicxUser) : this(
        username = user.username,
        password = user.password,
        name = user.name,
        email = user.email,
        hasImage = user.userImage != null
    )

    companion object {
        val GRANTED_AUTHORITIES = setOf(SimpleGrantedAuthority("USER"))
    }
}