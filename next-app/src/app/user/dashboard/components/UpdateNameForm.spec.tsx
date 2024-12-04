import {act, render, screen} from "@testing-library/react"
import UpdateNameForm from "@/app/user/dashboard/components/UpdateNameForm"
import {changeInputValueByInput} from "@/testUtils"
import {
    callsOnCancelTest, callsOnUpdateSuccessTest,
    displaysBackendValidationErrorTest,
    displaysSpinnerTest, mockOnCancel, mockOnUpdateSuccess,
    userLogoutTest
} from "@/app/user/dashboard/components/updateFormTestUtils"

jest.mock("next-auth/react", () => ({
    getSession: jest.fn(),
    signOut: jest.fn()
}))

const setupForm = async () => {
    const renderResult = await act(() => render(
        <UpdateNameForm
            onUpdateSuccess={mockOnUpdateSuccess}
            onCancel={mockOnCancel}
            currentName="John Doe"/>
    ))

    return {
        nameInput: renderResult.getByTestId("name-input"),
        saveButton: renderResult.getByRole("button", {name: "Save"}),
        closeButton: renderResult.getByRole("button", {name: "Cancel"})
    }
}

describe("UpdateNameForm", () => {
    describe("Layout", () => {
        let nameInput: HTMLElement
        let saveButton: HTMLElement
        let closeButton: HTMLElement

        beforeEach(async () => ({nameInput, saveButton, closeButton} = await setupForm()))

        it("has a name input with current name when provided", async () =>
            expect(nameInput).toHaveValue("John Doe"))

        it("has a disabled save button", async () =>
            expect(saveButton).toBeDisabled())

        it("has an enabled close button", async () =>
            expect(closeButton).toBeEnabled())
    })

    describe("Input field interactions", () => {
        let nameInput: HTMLElement
        let saveButton: HTMLElement

        beforeEach(async () => ({nameInput, saveButton} = await setupForm()))

        it("has required validation error message given blank name", async () => {
            await changeInputValueByInput(nameInput, "a")
            await changeInputValueByInput(nameInput, "")
            expect(screen.queryByText("Name is required")).toBeInTheDocument()
        })
        it("has validation error message given too short name", async () => {
            await changeInputValueByInput(nameInput, "a".repeat(3))
            expect(screen.queryByText("It must have a minimum of 4 characters")).toBeInTheDocument()
        })
        it("has validation error message given too long name", async () => {
            await changeInputValueByInput(nameInput, "a".repeat(256))
            expect(screen.queryByText("It must have a maximum of 255 characters")).toBeInTheDocument()
        })

        it("has a disabled submit button when validation errors", async () => {
            await changeInputValueByInput(nameInput, "a".repeat(3))
            expect(saveButton).toBeDisabled()
        })

        it("has an enabled submit button when input is changed", async () => {
            await changeInputValueByInput(nameInput, "a".repeat(4))
            expect(saveButton).toBeEnabled()
        })
    })

    describe("Button interactions", () => {
        let nameInput: HTMLElement

        beforeEach(async () => {
            ({nameInput} = await setupForm())
            await changeInputValueByInput(nameInput, "John Doe Updated")
        })

        it("displays backend validation error when errors", async () =>
            await displaysBackendValidationErrorTest())

        it("displays spinner when data is loading", async () =>
            await displaysSpinnerTest())

        it("calls onUpdateSuccess", async () =>
            await callsOnUpdateSuccessTest())

        it("calls onCancel", async () =>
            callsOnCancelTest())

        it("logs user out when token has expired", async () =>
            await userLogoutTest())
    })
})