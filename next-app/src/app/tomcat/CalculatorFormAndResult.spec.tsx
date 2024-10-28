import {act, fireEvent, render, screen} from "@testing-library/react"
import CalculatorFormAndResult from "@/app/tomcat/CalculatorFormAndResult"

const changeEvent = (content: string) => ({target: {value: content}})

const changeInputValue = async (label: string, value: string) => {
    const input = screen.getByLabelText(label)
    await act(() => fireEvent.change(input, changeEvent(value)))
}

const setupForSubmit = async () => {
    await changeInputValue("First Value", "1")
    await changeInputValue("Second Value", "2")
}

const validAddResponse = {
    firstValue: 1,
    secondValue: 2,
    operation: "PLUS",
    result: 3
}

const validSubtractResponse = {
    ...validAddResponse,
    operation: "MINUS",
    result: -1
}

describe("CalculatorFormAndResult", () => {
    describe("Layout", () => {
        beforeEach(() => render(<CalculatorFormAndResult/>))

        it("displays input for First Value", () => {
            expect(screen.queryByLabelText("First Value")).toBeInTheDocument()
        })

        it("displays input for Second Value", () => {
            expect(screen.queryByLabelText("Second Value")).toBeInTheDocument()
        })

        it("displays Add submit button", () => {
            expect(screen.queryByRole("button", {name: "Add"})).toBeInTheDocument()
        })

        it("displays Subtract submit button", () => {
            expect(screen.queryByRole("button", {name: "Subtract"})).toBeInTheDocument()
        })

        it("displays disabled Add submit button", () => {
            expect(screen.getByRole("button", {name: "Add"})).toBeDisabled()
        })

        it("displays disabled Subtract submit button", () => {
            expect(screen.getByRole("button", {name: "Subtract"})).toBeDisabled()
        })
    })

    describe("Input fields interactions", () => {
        beforeEach(async () => {
            render(<CalculatorFormAndResult/>)
            await setupForSubmit()
        })

        it("enables Add submit button when all fields are valid", () => {
            expect(screen.getByRole("button", {name: "Add"})).toBeEnabled()
        })

        it("enables Subtract submit button when all fields are valid", () => {
            expect(screen.getByRole("button", {name: "Subtract"})).toBeEnabled()
        })

        it("displays validation error when First Value is missing", async () => {
            await changeInputValue("First Value", "")
            expect(screen.queryByText("First value must be a number")).toBeInTheDocument()
        })

        it("displays validation error when Second Value is missing", async () => {
            await changeInputValue("Second Value", "")
            expect(screen.queryByText("Second value must be a number")).toBeInTheDocument()
        })
    })

    describe("Submit button interactions", () => {
        beforeEach(async () => {
            render(<CalculatorFormAndResult/>)
            await setupForSubmit()
        })

        it("displays add result when API returns valid response", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validAddResponse))
            await act(() => fireEvent.click(screen.getByRole("button", {name: "Add"})))

            expect(screen.queryByText("1 + 2 = 3")).toBeInTheDocument()
        })

        it("displays subtract result when API returns valid response", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validSubtractResponse))
            await act(() => fireEvent.click(screen.getByRole("button", {name: "Subtract"})))

            expect(screen.queryByText("1 - 2 = -1")).toBeInTheDocument()
        })
    })
})