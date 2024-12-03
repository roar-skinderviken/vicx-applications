import {act, render, screen} from "@testing-library/react"
import {changeInputValueByInput} from "@/testUtils"
import {
    displaysBackendValidationErrorTest, displaysSpinnerTest, userLogoutTest
} from "@/app/user/dashboard/components/updateFormTestUtils"
import UpdatePasswordForm from "@/app/user/dashboard/components/UpdatePasswordForm"

jest.mock("next-auth/react", () => ({
    getSession: jest.fn(),
    signOut: jest.fn()
}))

const setupForm = async () => {
    const renderResult = await act(() => render(
        <UpdatePasswordForm cardTitle="Edit password"/>
    ))

    return {
        currentPasswordInput: renderResult.getByTestId("currentPassword-input"),
        newPasswordInput: renderResult.getByTestId("newPassword-input"),
        confirmPasswordInput: renderResult.getByTestId("confirmPassword-input"),
        submitButton: renderResult.getByRole("button", {name: "Submit"}),
    }
}

describe("UpdatePasswordForm", () => {
    describe("Layout", () => {
        let currentPasswordInput: HTMLElement
        let newPasswordInput: HTMLElement
        let confirmPasswordInput: HTMLElement
        let submitButton: HTMLElement

        beforeEach(async () => ({
            currentPasswordInput,
            newPasswordInput,
            confirmPasswordInput,
            submitButton
        } = await setupForm()))

        it("has input for current password with type password", () =>
            expect(currentPasswordInput).toHaveProperty("type", "password"))

        it("has input for new password with type password", () =>
            expect(newPasswordInput).toHaveProperty("type", "password"))

        it("has input for confirm password with type password", () =>
            expect(confirmPasswordInput).toHaveProperty("type", "password"))

        it("has a disabled submit button", () =>
            expect(submitButton).toBeDisabled())
    })

    describe("Input interactions", () => {
        let currentPasswordInput: HTMLElement
        let newPasswordInput: HTMLElement
        let confirmPasswordInput: HTMLElement

        beforeEach(async () => ({
            currentPasswordInput,
            newPasswordInput,
            confirmPasswordInput
        } = await setupForm()))

        it("displays required validation error message given blank current password", async () => {
            await changeInputValueByInput(currentPasswordInput, "a")
            await changeInputValueByInput(currentPasswordInput, "")
            expect(screen.queryByText("Current password is required")).toBeInTheDocument()
        })
        it("displays validation error message given too short current password", async () => {
            await changeInputValueByInput(currentPasswordInput, "Aa1")
            expect(screen.queryByText("It must have a minimum of 4 characters")).toBeInTheDocument()
        })

        it("displays required validation error message given blank password", async () => {
            await changeInputValueByInput(newPasswordInput, "a")
            await changeInputValueByInput(newPasswordInput, "")
            expect(screen.queryByText("Password is required")).toBeInTheDocument()
        })
        it("displays validation error message given too short password", async () => {
            await changeInputValueByInput(newPasswordInput, "Aa1")
            expect(screen.queryByText("It must have a minimum of 8 characters")).toBeInTheDocument()
        })
        it("displays validation error message given too long password", async () => {
            await changeInputValueByInput(newPasswordInput, "Aa1".repeat(87))
            expect(screen.queryByText("It must have a maximum of 255 characters")).toBeInTheDocument()
        })
        it("displays validation error message given invalid password", async () => {
            await changeInputValueByInput(newPasswordInput, "a".repeat(8))
            expect(screen.queryByText(
                "Password must have at least one uppercase, one lowercase letter, and one number")).toBeInTheDocument()
        })

        it("displays required validation error message given blank repeat password", async () => {
            await changeInputValueByInput(confirmPasswordInput, "a")
            await changeInputValueByInput(confirmPasswordInput, "")
            expect(screen.queryByText("Confirm password is required")).toBeInTheDocument()
        })
        it("displays validation error message given password mismatch", async () => {
            await changeInputValueByInput(confirmPasswordInput, "password")
            expect(screen.queryByText("Passwords must match")).toBeInTheDocument()
        })
    })

    describe("Button interactions", () => {
        let currentPasswordInput: HTMLElement
        let newPasswordInput: HTMLElement
        let confirmPasswordInput: HTMLElement

        const validPassword = "P4ssword"

        beforeEach(async () => {
            ({
                currentPasswordInput,
                newPasswordInput,
                confirmPasswordInput
            } = await setupForm())

            await changeInputValueByInput(currentPasswordInput, validPassword)
            await changeInputValueByInput(newPasswordInput, validPassword)
            await changeInputValueByInput(confirmPasswordInput, validPassword)
        })

        it("displays backend validation error when errors", async () =>
            await displaysBackendValidationErrorTest("Submit"))

        it("displays spinner when data is loading", async () =>
            await displaysSpinnerTest("Submit"))

        it("logs user out when token has expired", async () =>
            await userLogoutTest("Submit"))
    })
})
