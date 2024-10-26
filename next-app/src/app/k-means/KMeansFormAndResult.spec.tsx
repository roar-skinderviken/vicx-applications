import {act, fireEvent, render, screen} from "@testing-library/react"
import KMeansFormAndResult from "@/app/k-means/KMeansFormAndResult"


const changeEvent = (content: string) => ({target: {value: content}})

const changeInputValue = async (label: string, value: string) => {
    const input = screen.getByLabelText(label)
    await act(() => fireEvent.change(input, changeEvent(value)))
}

const setupForSubmit = async () => {
    await changeInputValue("Max Score", "111")
    await changeInputValue("Fail Score", "22")
    await changeInputValue("Scores (comma separated)", "11,22,33,44,55,66")
}

const errorResponse = {error: "Error message"}
const validResponse = {
    111: "A",
    90: "B",
    70: "C",
    50: "D",
    30: "E",
    1: "F",
}

describe("KMeansFormAndResult", () => {
    describe("Layout", () => {
        beforeEach(() => render(<KMeansFormAndResult/>))

        it("displays input for Max Score", () => {
            expect(screen.queryByLabelText("Max Score")).toBeInTheDocument()
        })

        it("displays input for Fail Score", () => {
            expect(screen.queryByLabelText("Fail Score")).toBeInTheDocument()
        })

        it("displays input for Scores", () => {
            expect(screen.queryByLabelText("Scores (comma separated)")).toBeInTheDocument()
        })

        it("displays input for Max Iterations", () => {
            expect(screen.queryByLabelText("Max Iterations")).toBeInTheDocument()
        })

        it("displays submit button", () => {
            expect(screen.queryByRole("button")).toBeInTheDocument()
        })

        it("displays disabled submit button", () => {
            expect(screen.getByRole("button")).toBeDisabled()
        })
    })

    describe("Input fields interactions", () => {
        beforeEach(async () => {
            render(<KMeansFormAndResult/>)
            await setupForSubmit()
        })

        it("enables submit button when all fields are valid", async () => {
            expect(screen.getByRole("button")).toBeEnabled()
        })

        it("displays validation error when Max Score is missing", async () => {
            await changeInputValue("Max Score", "")
            expect(screen.queryByText("Max Score must be a number")).toBeInTheDocument()
        })

        it("displays validation error when Max Score is too low", async () => {
            await changeInputValue("Max Score", "9")
            expect(screen.queryByText("Max Score must be greater than 10")).toBeInTheDocument()
        })

        it("displays validation error when Max Score is too high", async () => {
            await changeInputValue("Max Score", "1001")
            expect(screen.queryByText("Max Score must be less than 1000")).toBeInTheDocument()
        })

        it("displays validation error when Fail Score is missing", async () => {
            await changeInputValue("Fail Score", "")
            expect(screen.queryByText("Fail Score must be a number")).toBeInTheDocument()
        })

        it("displays validation error when Fail Score is too low", async () => {
            await changeInputValue("Fail Score", "-1")
            expect(screen.queryByText("Fail Score must be equal or greater than 0")).toBeInTheDocument()
        })

        it("displays validation error when Scores is missing", async () => {
            await changeInputValue("Scores (comma separated)", "")
            expect(screen.queryByText("Scores are required")).toBeInTheDocument()
        })

        it("displays validation error when too few Scores", async () => {
            await changeInputValue("Scores (comma separated)", "11,22")
            expect(screen.queryByText("There should be at least 5 scores")).toBeInTheDocument()
        })

        it("displays validation error when Scores are not numbers", async () => {
            await changeInputValue("Scores (comma separated)", "a,1,2,3,5")
            expect(screen.queryByText("All scores must be numbers")).toBeInTheDocument()
        })

        it("displays validation error when Max Iterations is missing", async () => {
            await changeInputValue("Max Iterations", "")
            expect(screen.queryByText("Max Iterations must be a number")).toBeInTheDocument()
        })

        it("displays validation error when Max Iterations is too low", async () => {
            await changeInputValue("Max Iterations", "0")
            expect(screen.queryByText("Max Iterations must be greater than 0")).toBeInTheDocument()
        })

        it("displays validation error when Max Iterations is too low", async () => {
            await changeInputValue("Max Iterations", "1000")
            expect(screen.queryByText("Max Iterations must be less than 1000")).toBeInTheDocument()
        })
    })

    describe("Submit button interactions", () => {
        beforeEach(async () => {
            render(<KMeansFormAndResult/>)
            await setupForSubmit()
        })

        it("displays error message when API returns an error", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(errorResponse))
            await act(() => fireEvent.click(screen.getByRole("button")))
            expect(screen.queryByText(errorResponse.error)).toBeInTheDocument()
        })

        it("displays result when API returns valid response", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validResponse))
            await act(() => fireEvent.click(screen.getByRole("button")))

            Object.entries(validResponse).forEach(([score, grade]) => {
                expect(screen.queryByText(`Score: ${score}, Grade: ${grade}`)).toBeInTheDocument()
            })
        })
    })
})