package no.vicx.ktor.user

import no.vicx.ktor.db.model.VicxUser
import no.vicx.ktor.user.vm.UserVm

fun VicxUser.toViewModel() =
    UserVm(
        username,
        name,
        email,
        userImage != null,
    )
