import React from "react"
import {render, screen} from "@testing-library/react"
import {useFormContext} from "react-hook-form"
import ValidatedTextInput from "@/components/ValidatedTextInput"

jest.mock("react-hook-form", () => ({
    useFormContext: jest.fn()
}))

describe("ValidatedTextInput", () => {
    describe("Layout", () => {
        const mockRegister = jest.fn()
        const mockErrors: Record<string, { message?: string }> = {}

        beforeEach(() => {
            jest.clearAllMocks();

            (useFormContext as jest.Mock).mockReturnValue({
                register: mockRegister,
                formState: {errors: mockErrors}
            })
        })

        it("displays label text", () => {
            render(<ValidatedTextInput name="testInput" label="Test Label"/>)
            expect(screen.queryByLabelText("Test Label")).toBeInTheDocument()
        })

        it("displays input with type 'text'", () => {
            render(<ValidatedTextInput name="testInput" label="Test Label"/>)
            const inputField = screen.getByLabelText("Test Label")
            expect(inputField).toHaveAttribute("type", "text")
        })

        it("applies success styles when there are no errors", () => {
            render(<ValidatedTextInput name="testInput" label="Test Label"/>)
            const inputField = screen.getByLabelText("Test Label")
            expect(inputField).toHaveClass("text-green-900")
        })

        it("applies error styles when there are errors", () => {
            mockErrors.testInput = { message: 'This field is required' }

            render(<ValidatedTextInput name="testInput" label="Test Label"/>)

            const inputField = screen.getByLabelText("Test Label")
            expect(inputField).toHaveClass("text-red-900")
        })

        it("displays error when there are errors", () => {
            mockErrors.testInput = { message: 'This field is required' }

            render(<ValidatedTextInput name="testInput" label="Test Label"/>)

            expect(screen.queryByText("This field is required")).toBeInTheDocument()
        })

        it("displays default value when provided", () => {
            render(<ValidatedTextInput name="testInput" label="Test Label" defaultValue="Default Value"/>)
            expect(screen.getByLabelText("Test Label")).toHaveValue("Default Value")
        })
    })
})
