import {fireEvent, render, screen} from "@testing-library/react"
import PreviousCalculations, {formatDate} from "@/app/tomcat/PreviousCalculations"
import {CalculationResult} from "@/app/tomcat/CalculatorFormAndResult"
import resetAllMocks = jest.resetAllMocks


const dateInTest = new Date()

const createValidCalculation = (username?: string): CalculationResult => ({
    id: 1,
    firstValue: 1,
    secondValue: 2,
    operation: "PLUS",
    result: 3,
    username: username,
    createdAt: dateInTest
})

describe("PreviousCalculations", () => {
    describe("Layout", () => {

        it("does not display header when no calculations is provided", () => {
            render(<PreviousCalculations calculations={[]}/>)

            expect(screen.queryByText("Previous results on this server")).not.toBeInTheDocument()
        })

        it("displays header when calculations is provided", () => {
            render(<PreviousCalculations calculations={[createValidCalculation()]}/>)

            expect(screen.queryByText("Previous results on this server")).toBeInTheDocument()
        })

        it("does not display 'Delete selected' button when username is not provided", () => {
            render(<PreviousCalculations
                calculations={[createValidCalculation()]}/>
            )

            expect(screen.queryByRole("button")).not.toBeInTheDocument()
        })

        it("displays 'Delete selected' button when username and data is provided", () => {
            render(<PreviousCalculations
                username="user1"
                calculations={[createValidCalculation()]}/>
            )

            const deleteSelectedButton = screen.queryByRole("button")
            expect(deleteSelectedButton).toHaveTextContent("Delete selected")
            expect(deleteSelectedButton).toBeDisabled()
        })

        it("displays header row without 'Select all' checkbox when calculations is provided and no username", () => {
            render(<PreviousCalculations calculations={[createValidCalculation()]}/>)

            expect(screen.queryByText("Select all")).not.toBeInTheDocument()
            expect(screen.queryByText("Calculation")).toBeInTheDocument()
            expect(screen.queryByText("Username")).toBeInTheDocument()
            expect(screen.queryByText("Date")).toBeInTheDocument()
        })

        it("disables 'Select all' checkbox when there is no items to select", () => {
            render(<PreviousCalculations
                username="user1"
                calculations={[createValidCalculation("user2")]}/>)

            expect(screen.queryByLabelText("Select all")).toBeDisabled()
        })

        it("displays 'Select all' checkbox when there are items to select", () => {
            render(<PreviousCalculations
                username="user1"
                calculations={[createValidCalculation("user1")]}/>
            )

            expect(screen.queryByText("Select all")).toBeInTheDocument()
        })

        it("displays body row when data is provided and username is not provided", () => {
            render(<PreviousCalculations
                calculations={[createValidCalculation()]}/>
            )

            expect(screen.queryByText("1 + 2 = 3")).toBeInTheDocument()
            expect(screen.queryByText("Anonymous")).toBeInTheDocument()
            expect(screen.queryByText(formatDate(dateInTest))).toBeInTheDocument()
        })

        it("displays body row with username when calculation has username", () => {
            render(<PreviousCalculations
                calculations={[createValidCalculation("user1")]}/>
            )

            expect(screen.queryByText("user1")).toBeInTheDocument()
        })

        it("displays body row without checkbox when username is not provided", () => {
            render(<PreviousCalculations
                calculations={[createValidCalculation()]}/>
            )

            expect(screen.queryByTestId("checkbox-1")).not.toBeInTheDocument()
        })

        it("displays body row without checkbox when item is not owned by user", () => {
            render(<PreviousCalculations
                username="user1"
                calculations={[createValidCalculation()]}/>
            )

            expect(screen.queryByTestId("checkbox-1")).not.toBeInTheDocument()
        })

        it("displays body row with checkbox when item is owned by user", () => {
            render(<PreviousCalculations
                username="user1"
                calculations={[createValidCalculation("user1")]}/>
            )

            expect(screen.queryByTestId("checkbox-1")).toBeInTheDocument()
        })

        it("displays 'Fetch More' button when hasMorePages is true", () => {
            render(<PreviousCalculations
                username="user1"
                hasMorePages={true}
                calculations={[createValidCalculation("user1")]}/>
            )

            expect(screen.queryByText("Fetch More")).toBeInTheDocument()
        })
    })

    describe("Checkbox interactions", () => {
        let checkbox: HTMLInputElement

        beforeEach(() => {
            render(<PreviousCalculations
                username="user1"
                calculations={[createValidCalculation("user1")]}/>
            )
            checkbox = screen.getByTestId("checkbox-1")
        })

        it("displays checkbox as checked when checked", () => {
            fireEvent.click(checkbox)

            expect(checkbox).toBeChecked()
        })

        it("displays checkbox as not checked when unchecked", () => {
            fireEvent.click(checkbox)
            fireEvent.click(checkbox)

            expect(checkbox).not.toBeChecked()
        })

        it("displays checkbox as checked when 'Select all' is checked", async () => {
            const selectAllCheckbox = screen.getByLabelText("Select all")

            fireEvent.click(selectAllCheckbox)

            expect(checkbox).toBeChecked()
        })

        it("displays 'Select all' checkbox as checked when all items are checked", async () => {
            fireEvent.click(checkbox)

            expect(screen.getByLabelText("Select all")).toBeChecked()
        })

        it("displays 'Select all' checkbox as unchecked when not all items are checked", async () => {
            const selectAllCheckbox = screen.getByLabelText("Select all")

            fireEvent.click(selectAllCheckbox)
            fireEvent.click(checkbox)

            expect(selectAllCheckbox).not.toBeChecked()
        })

        it("enables the 'Delete selected' button when selected items", async () => {
            fireEvent.click(checkbox)

            const button = screen.getByRole("button")

            expect(button).toBeEnabled()
        })
    })

    describe("Button interactions", () => {
        let deleteSelectedButton: HTMLInputElement
        let fetchMoreButton: HTMLInputElement

        const mockOnDelete = jest.fn()
        const mockOnFetchMore = jest.fn()

        beforeEach(() => {
            resetAllMocks()
            render(<PreviousCalculations
                    username="user1"
                    calculations={[createValidCalculation("user1")]}
                    onDelete={mockOnDelete}
                    hasMorePages={true}
                    onFetchMore={mockOnFetchMore}
                />
            )
            fireEvent.click(screen.getByTestId("checkbox-1"))
            deleteSelectedButton = screen.getAllByRole("button")[0] as HTMLInputElement
            fetchMoreButton = screen.getAllByRole("button")[1] as HTMLInputElement
        })

        it("calls onDeleteItems callback when 'Delete selected' is clicked", () => {
            fireEvent.click(deleteSelectedButton)

            expect(mockOnDelete).toHaveBeenCalledWith([1])
        })

        it("displays spinner when 'Delete selected' is clicked", () => {
            fireEvent.click(deleteSelectedButton)

            expect(screen.queryByText("Loading...")).toBeInTheDocument()
        })

        it("calls onFetchMore callback when 'Fetch More' is clicked", () => {
            fireEvent.click(fetchMoreButton)

            expect(mockOnFetchMore).toHaveBeenCalled()
        })

        it("displays spinner when 'Fetch More' is clicked", () => {
            fireEvent.click(fetchMoreButton)

            expect(screen.queryByText("Loading...")).toBeInTheDocument()
        })
    })
})