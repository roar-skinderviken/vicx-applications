import {act, fireEvent, render, screen, waitFor} from "@testing-library/react"
import CalculatorFormAndResult, {
    CALC_BACKEND_BASE_URL,
    CALC_NEXT_BACKEND_URL
} from "@/app/tomcat/CalculatorFormAndResult"
import {getSession} from "next-auth/react"
import {CustomSession} from "@/types/authTypes"
import {changeInputValue, delayedResponse} from "@/testUtils"

jest.mock("next-auth/react", () => ({
    getSession: jest.fn()
}))

const mockGetSession = getSession as jest.Mock

const setupForSubmit = async () => {
    await changeInputValue("First Value", "1")
    await changeInputValue("Second Value", "2")
}

const sessionUser = (roles: string[]) => ({
    id: "1",
    name: "user1",
    roles: roles
})

const validSession: CustomSession = {
    expires: Date.now().toString(),
    user: sessionUser(["ROLE_USER"])
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

const previousResult = {
    content: [{
        id: 49,
        firstValue: 1,
        secondValue: 12,
        operation: "PLUS",
        result: 13,
        username: "user1",
        createdAt: "2024-11-11T21:17:16.748138"
    }],
    pageable: {
        pageNumber: 0,
        pageSize: 10,
    },
    last: false
}

const createValidRequest = (firstValue: number, secondValue: number, operation: string) => {
    return {
        body: JSON.stringify({
            operation,
            secondValue,
            firstValue,
        }),
        headers: {"Content-Type": "application/json"},
        method: "POST",
    }
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
        const expectSpanValuesToBeInTheDocument = (values: string[]) => {
            values.forEach((value) => {
                expect(screen.queryByText(value)?.closest("span")).toBeInTheDocument()
            })
        }

        const runCalculationTest = async (operation: "PLUS" | "MINUS", authenticated: boolean = false) => {
            const isAddition = operation === "PLUS"
            const validResponse = isAddition ? validAddResponse : validSubtractResponse

            const expectedSign = isAddition ? "+" : "-"
            const expectedResult = isAddition ? "3" : "-1"

            if (authenticated) mockGetSession.mockResolvedValueOnce(validSession)

            fetchMock
                .mockResponseOnce(async () => await delayedResponse(JSON.stringify(validResponse), 100))
                .mockResponseOnce(JSON.stringify(previousResult))

            const button = screen.getByRole("button", {name: isAddition ? "Add" : "Subtract"})
            const otherButton = screen.getByRole("button", {name: isAddition ? "Subtract" : "Add"})

            await act(() => fireEvent.click(button))

            expect(button).toHaveTextContent("Loading...")
            expect(otherButton).not.toHaveTextContent("Loading...")

            expect(button).toBeDisabled()
            expect(otherButton).toBeDisabled()

            await waitFor(() =>
                expect(button).not.toHaveTextContent("Loading..."))

            expectSpanValuesToBeInTheDocument(["1", expectedSign, "2", "=", expectedResult])

            // Verify API calls
            const backendUrl = authenticated ? CALC_NEXT_BACKEND_URL : CALC_BACKEND_BASE_URL
            expect(fetchMock).toHaveBeenCalledWith(backendUrl, createValidRequest(1, 2, operation))
            expect(fetchMock).toHaveBeenCalledWith(`${CALC_BACKEND_BASE_URL}?page=0`)
        }

        beforeEach(async () => {
            fetchMock.resetMocks()
            mockGetSession.mockResolvedValue(null)
            render(<CalculatorFormAndResult/>)
            await setupForSubmit()
        })

        it("displays add result when API returns valid response, user not logged in", async () => {
            await runCalculationTest("PLUS")
        })

        it("calls Next route API endpoint with add when user has role 'ROLE_USER'", async () => {
            await runCalculationTest("PLUS", true)
        })

        it("displays subtract result when API returns valid response", async () => {
            await runCalculationTest("MINUS")
        })

        it("calls Next route API endpoint with subtract when user has role 'ROLE_USER'", async () => {
            await runCalculationTest("MINUS", true)
        })

        it("displays list of previous results when returned by API", async () => {
            await runCalculationTest("MINUS", true)

            expect(screen.queryByRole("heading", {level: 3})).toHaveTextContent("Previous results on this server")

            expect(screen.queryByText(previousResult.content[0].username)).toBeInTheDocument()
        })

        it("hides previous calculation result when resubmitting form", async () => {
            fetchMock
                .mockResponseOnce(JSON.stringify(validAddResponse))
                .mockResponseOnce(JSON.stringify(previousResult))
                .mockResponseOnce(async () => await delayedResponse(JSON.stringify(validAddResponse), 100))
                .mockResponseOnce(JSON.stringify(previousResult))

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Add"})))
            await act(() => fireEvent.click(screen.getByRole("button", {name: "Subtract"})))

            expect(screen.queryByText("Calculation Result")).not.toBeInTheDocument()

            await waitFor(() =>
                expect(screen.queryByText("Calculation Result")).toBeInTheDocument())
        })
    })
})