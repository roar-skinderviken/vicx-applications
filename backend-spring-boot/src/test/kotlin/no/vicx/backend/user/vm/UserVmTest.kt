package no.vicx.backend.user.vm

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import no.vicx.backend.user.UserTestUtils.createValidVicxUser
import no.vicx.backend.user.vm.UserVm.Companion.fromVicxUser
import no.vicx.database.user.UserImage
import org.springframework.data.jpa.domain.AbstractPersistable_.id

class UserVmTest : StringSpec({

    "fromVicxUser given valid user without user image then expect populate view model" {
        val vicxUser = createValidVicxUser()

        val viewModel = fromVicxUser(vicxUser)

        assertSoftly(viewModel) {
            id shouldBe vicxUser.id
            username shouldBe vicxUser.username
            name shouldBe vicxUser.name
            email shouldBe vicxUser.email
            hasImage shouldBe false
        }
    }

    "fromVicxUser given valid user with user image then expect populate view model" {
        val vicxUser = createValidVicxUser().apply { userImage = UserImage() }

        val viewModel = fromVicxUser(vicxUser)

        viewModel.hasImage shouldBe true
    }
})