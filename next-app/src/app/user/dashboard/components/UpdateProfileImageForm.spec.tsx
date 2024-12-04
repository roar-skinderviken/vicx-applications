import UpdateProfileImageForm from "@/app/user/dashboard/components/UpdateProfileImageForm"
import {act, fireEvent, render, screen, waitFor} from "@testing-library/react"
import {delayedResponse} from "@/testUtils"
import {mockGetSession, mockOnCancel, validSession} from "@/app/user/dashboard/components/updateFormTestUtils"

const mockGifFile = new File(["file content"], "gif-file.gif", {type: "image/gif"})
const mockPngFile = new File(["file content"], "png-file.png", {type: "image/png"})

const mockOnUploadSuccess = jest.fn()

jest.mock("next-auth/react", () => ({
    getSession: jest.fn(),
    signOut: jest.fn()
}))

const setupForm = async () =>
    await act(() => render(<UpdateProfileImageForm
        onUploadSuccess={mockOnUploadSuccess}
        onCancel={mockOnCancel}/>))

const setupForSubmit = async () => {
    const fileInput = screen.getByTestId("image-update-file-input")
    await act(() => fireEvent.change(fileInput, {target: {files: [mockPngFile]}}))
}

describe("UpdateProfileImageForm", () => {
    describe("layout", () => {
        beforeEach(async () => await setupForm())

        it("has a 'No Image' div", () =>
            expect(screen.queryByText("No Image")).toBeInTheDocument())

        it("has a file inpout", () =>
            expect(screen.queryByTestId("image-update-file-input")).toBeInTheDocument())

        it("has a save button", () =>
            expect(screen.queryByRole("button", {name: "Save"})).toBeInTheDocument())

        it("has a disabled save button", () =>
            expect(screen.queryByRole("button", {name: "Save"})).toBeDisabled())

        it("has a cancel button", () =>
            expect(screen.queryByRole("button", {name: "Cancel"})).toBeInTheDocument())

        it("displays spinner when data is loading", async () => {
            mockGetSession.mockResolvedValue(validSession)
            fetchMock.mockResponseOnce(
                async () => await delayedResponse("", 300))

            await setupForSubmit()

            await act(() => fireEvent.click(screen.getByRole("button", {name: "Save"})))

            await waitFor(() =>
                expect(screen.queryByText("Loading...")).toBeInTheDocument())
        })
    })

    describe("Input interactions", () => {
        beforeEach(async () => await setupForm())

        it("displays file type validation error message given invalid file format", async () => {
            const fileInput = screen.getByTestId("image-update-file-input")
            await act(() => fireEvent.change(fileInput, {target: {files: [mockGifFile]}}))

            expect(screen.queryByText("Only PNG and JPG files are allowed")).toBeInTheDocument()
        })

        it("displays file size validation error message given too large file", async () => {
            Object.defineProperty(mockPngFile, "size", {value: 60_000}) // 60 KB

            const fileInput = screen.getByTestId("image-update-file-input")
            await act(() => fireEvent.change(fileInput, {target: {files: [mockPngFile]}}))

            expect(screen.queryByText("File size must not exceed 50 KB")).toBeInTheDocument()
        })
    })

    describe("Button interactions", () => {
        beforeEach(async () => await setupForm())

        it("invokes onCancel when cancel button is clicked", async () => {
            fireEvent.click(screen.getByRole("button", {name: "Cancel"}))
            expect(mockOnCancel).toHaveBeenCalled()
        })

        it("invokes onUploadSuccess after upload", async () => {
            mockGetSession.mockResolvedValue(validSession)
            fetchMock.mockResponseOnce("", {status: 201})
            await setupForSubmit()
            await act(() => fireEvent.click(screen.getByRole("button", {name: "Save"})))

            await waitFor(() => expect(mockOnUploadSuccess).toHaveBeenCalled())
        })
    })
})