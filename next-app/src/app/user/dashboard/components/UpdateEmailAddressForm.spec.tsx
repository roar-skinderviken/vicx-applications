import {act, render, screen} from "@testing-library/react"
import {changeInputValueByInput} from "@/testUtils"
import {
    callsOnEndEditTest,
    displaysBackendValidationErrorTest, displaysSpinnerTest, mockOmEndEdit,
    userLogoutTest
} from "@/app/user/dashboard/components/updateFormTestUtils"
import UpdateEmailAddressForm from "@/app/user/dashboard/components/UpdateEmailAddressForm"

jest.mock("next-auth/react", () => ({
    getSession: jest.fn(),
    signOut: jest.fn()
}))

const setupForm = async () => {
    const renderResult = await act(() => render(
        <UpdateEmailAddressForm onEndEdit={mockOmEndEdit} currentEmailAddress="john@doe.com"/>
    ))

    return {
        emailInput: renderResult.getByTestId("email-input"),
        saveButton: renderResult.getByRole("button", {name: "Save"}),
        closeButton: renderResult.getByRole("button", {name: "Close"})
    }
}

describe("UpdateEmailAddressForm", () => {
    describe("Layout", () => {
        let emailInput: HTMLElement
        let saveButton: HTMLElement
        let closeButton: HTMLElement

        beforeEach(async () => ({emailInput, saveButton, closeButton} = await setupForm()))

        it("has an email input with current email address when provided", async () =>
            expect(emailInput).toHaveValue("john@doe.com"))

        it("has a disabled submit button", async () =>
            expect(saveButton).toBeDisabled())

        it("has an enabled close button", async () =>
            expect(closeButton).toBeEnabled())
    })

    describe("Input field interactions", () => {
        let emailInput: HTMLElement
        let saveButton: HTMLElement

        beforeEach(async () => ({emailInput, saveButton} = await setupForm()))

        it("has required validation error message given blank name", async () => {
            await changeInputValueByInput(emailInput, "a")
            await changeInputValueByInput(emailInput, "")
            expect(screen.queryByText("Email is required")).toBeInTheDocument()
        })
        it("has validation error message given illegal email address", async () => {
            await changeInputValueByInput(emailInput, "aaa")
            expect(screen.queryByText("Please enter a valid email address")).toBeInTheDocument()
        })

        it("has a disabled submit button when validation errors", async () => {
            await changeInputValueByInput(emailInput, "a".repeat(3))
            expect(screen.queryByRole("button", {name: "Save"})).toBeDisabled()
        })

        it("has an enabled submit button when input is changed", async () => {
            await changeInputValueByInput(emailInput, "john@example.com")
            expect(saveButton).toBeEnabled()
        })
    })

    describe("Button interactions", () => {
        let emailInput: HTMLElement

        beforeEach(async () => {
            ({emailInput} = await setupForm())
            await changeInputValueByInput(emailInput, "john@example.com")
        })

        it("displays backend validation error when errors", async () =>
            await displaysBackendValidationErrorTest())

        it("displays spinner when data is loading", async () =>
            await displaysSpinnerTest())

        it("calls onEndEdit", async () =>
            callsOnEndEditTest())

        it("logs user out when token has expired", async () =>
            await userLogoutTest())
    })
})