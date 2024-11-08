import {act, fireEvent, render, screen} from "@testing-library/react"
import CalculatorFormAndResult, {CALC_BACKEND_BASE_URL} from "@/app/tomcat/CalculatorFormAndResult"
import {getSession} from "next-auth/react"
import {CustomSession} from "@/types/authTypes"
import clearAllMocks = jest.clearAllMocks

jest.mock("next-auth/react", () => ({
    getSession: jest.fn()
}))

const mockGetSession = getSession as jest.Mock

const changeEvent = (content: string) => ({target: {value: content}})

const changeInputValue = async (label: string, value: string) => {
    const input = screen.getByLabelText(label)
    await act(() => fireEvent.change(input, changeEvent(value)))
}

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

const dateInTests = new Date()

const formattedDateInTest = new Intl.DateTimeFormat('en-US', {
    dateStyle: 'medium',
    timeStyle: 'short'
}).format(dateInTests)

const previousResult = {
    ...validAddResponse,
    username: "user1",
    createdAt: dateInTests
}

const validResponseWithPreviousResults = {
    ...validAddResponse,
    previousResults: [previousResult]
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

        const userNotLoggedInBackendUrl = (firstValue: number, secondValue: number, operation: string) =>
            `${CALC_BACKEND_BASE_URL}/${firstValue}/${secondValue}/${operation}`

        const userLoggedInBackendUrl = (firstValue: number, secondValue: number, operation: string) =>
            `/api/calculator?first=${firstValue}&second=${secondValue}&operation=${operation}`

        beforeEach(async () => {
            clearAllMocks()
            mockGetSession.mockResolvedValue(null)
            render(<CalculatorFormAndResult/>)
            await setupForSubmit()
        })

        it("displays add result when API returns valid response, user not logged in", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validAddResponse))

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Add"})))

            expectSpanValuesToBeInTheDocument(["1", "+", "2", "=", "3"])
            expect(fetchMock).toHaveBeenCalledWith(
                userNotLoggedInBackendUrl(1, 2, "PLUS"))
        })

        it("calls Next route API endpoint with add when user has role 'ROLE_USER'", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validAddResponse))
            mockGetSession.mockResolvedValueOnce(validSession)

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Add"})))

            expect(fetchMock).toHaveBeenCalledWith(
                userLoggedInBackendUrl(1, 2, "PLUS"))
        })

        it("displays subtract result when API returns valid response", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validSubtractResponse))

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Subtract"})))

            expectSpanValuesToBeInTheDocument(["1", "-", "2", "=", "-1"])
            expect(fetchMock).toHaveBeenCalledWith(
                userNotLoggedInBackendUrl(1, 2, "MINUS"))
        })

        it("calls Next route API endpoint with subtract when user has role 'ROLE_USER'", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validSubtractResponse))
            mockGetSession.mockResolvedValueOnce(validSession)

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Subtract"})))

            expectSpanValuesToBeInTheDocument(["1", "-", "2", "=", "-1"])
            expect(fetchMock).toHaveBeenCalledWith(
                userLoggedInBackendUrl(1, 2, "MINUS"))
        })

        it("calls backend API endpoint when user is missing role 'ROLE_USER'", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validSubtractResponse))

            mockGetSession.mockResolvedValueOnce({
                ...validSession,
                user: sessionUser([]),
            })

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Subtract"})))

            expectSpanValuesToBeInTheDocument(["1", "-", "2", "=", "-1"])
            expect(fetchMock).toHaveBeenCalledWith(
                userNotLoggedInBackendUrl(1, 2, "MINUS"))
        })

        it("displays previous results when returned by API", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(validResponseWithPreviousResults))

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Subtract"})))

            expect(screen.queryByRole("heading", {level: 3})).toHaveTextContent("Previous results on this server")

            expect(screen.queryByText(previousResult.username)).toBeInTheDocument()
            expect(screen.queryByText(formattedDateInTest)).toBeInTheDocument()
        })
    })
})