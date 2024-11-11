import SubmitButtonWithSpinner from "@/app/tomcat/SubmitButtonWithSpinner"
import {fireEvent, render, screen} from "@testing-library/react"

const mockOnClick = jest.fn()

const renderComponent = (disabled: boolean = false, isLoading: boolean = false) => {
    render(<SubmitButtonWithSpinner
        buttonText="Add"
        disabled={disabled}
        isLoading={isLoading}
        onButtonClick={mockOnClick}
    />)
}

describe("SubmitButtonWithSpinner", () => {
    describe("Layout", () => {
        it("renders button with type submit", () => {
            renderComponent()
            expect(screen.queryByRole("button")).toHaveProperty("type", "submit")
        })

        it("renders enabled button when disabled is provided as false", () => {
            renderComponent()
            expect(screen.queryByRole("button")).toBeEnabled()
        })

        it("renders disabled button when disabled is provided as true", () => {
            renderComponent(true)
            expect(screen.queryByRole("button")).toBeDisabled()
        })

        it("renders button text when isLoading is provided as false", () => {
            renderComponent()
            expect(screen.queryByRole("button")).toHaveTextContent("Add")
        })

        it("renders spinner when isLoading is provided as true", () => {
            renderComponent(undefined, true)
            expect(screen.queryByRole("button")).toHaveTextContent("Loading...")
        })
    })

    describe("Button interactions", () => {
        it("calls onButtonClick on click", () => {
            renderComponent()

            fireEvent.click(screen.getByRole("button"))
            
            expect(mockOnClick).toHaveBeenCalled()
        })
    })
})