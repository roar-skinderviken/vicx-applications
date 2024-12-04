import {act, fireEvent, render, screen} from "@testing-library/react"
import UserSignupForm from "./UserSignupForm"
import {changeInputValue, delayedResponse} from "@/testUtils"

const errorResponse = {
    validationErrors: {
        username: "This name is in use",
        recaptchaToken: "Invalid reCAPTCHA, please wait to token expires and try again"
    }
}

const setupForSubmit = async () => {
    await changeInputValue("Username", "User1")
    await changeInputValue("Name", "John Doe")
    await changeInputValue("Email", "john@example.com")
    await changeInputValue("Password", "P4ssword")
    await changeInputValue("Repeat Password", "P4ssword")
    await act(() => fireEvent.click(screen.getByTestId("recaptcha-checkbox")))
}

jest.mock("react-google-recaptcha", () => {
    const MockReCAPTCHA = ({onChange}: { onChange: (token: string | null) => void }) => (
        <div>
            <input
                type="checkbox"
                data-testid="recaptcha-checkbox"
                onClick={() => onChange("mock-token")}
            />
            Mock ReCAPTCHA
        </div>
    )

    MockReCAPTCHA.displayName = "MockReCAPTCHA"
    return MockReCAPTCHA
})

describe("UserSignupForm", () => {

    describe("Layout", () => {
        beforeEach(() => render(<UserSignupForm reCaptchaSiteKey="recaptcha-site-key"/>))

        it("has fallback profile image", () => {
            expect(screen.queryByAltText("Profile Image")).toBeInTheDocument()
        })

        it("has input for username", () => {
            expect(screen.queryByLabelText("Username")).toBeInTheDocument()
        })

        it("has input for name", () => {
            expect(screen.queryByLabelText("Name")).toBeInTheDocument()
        })

        it("has input for email", () => {
            expect(screen.queryByLabelText("Email")).toBeInTheDocument()
        })

        it("has input for profile image", () => {
            const imageInput = screen.queryByLabelText("Profile Image")
            expect(imageInput).toBeInTheDocument()
            expect(imageInput).toHaveProperty("type", "file")
        })

        it("has input for password", () => {
            const passwordInput = screen.queryByLabelText("Password")
            expect(passwordInput).toBeInTheDocument()
            expect(passwordInput).toHaveProperty("type", "password")
        })

        it("has input for repeat password", () => {
            const repeatPasswordInput = screen.queryByLabelText("Repeat Password")
            expect(repeatPasswordInput).toBeInTheDocument()
            expect(repeatPasswordInput).toHaveProperty("type", "password")
        })

        it("has reCaptcha", () => {
            expect(screen.queryByText("Mock ReCAPTCHA")).toBeInTheDocument()
        })

        it("has a disabled submit button", () => {
            const submitButton = screen.queryByRole("button")
            expect(submitButton).toBeInTheDocument()
            expect(submitButton).toBeDisabled()
        })
    })

    describe("Input interactions", () => {
        beforeEach(() => render(<UserSignupForm reCaptchaSiteKey="recaptcha-site-key"/>))

        it("displays required validation error message given blank username", async () => {
            await changeInputValue("Username", "a")
            await changeInputValue("Username", "")
            expect(screen.queryByText("Username is required")).toBeInTheDocument()
        })
        it("displays validation error message given too short username", async () => {
            await changeInputValue("Username", "a".repeat(3))
            expect(screen.queryByText("It must have a minimum of 4 characters")).toBeInTheDocument()
        })
        it("displays validation error message given too long username", async () => {
            await changeInputValue("Username", "a".repeat(256))
            expect(screen.queryByText("It must have a maximum of 255 characters")).toBeInTheDocument()
        })
        it("displays validation error message given username with blank", async () => {
            await changeInputValue("Username", "John Doe")
            expect(screen.queryByText("Username can only contain letters, numbers, hyphens, and underscores")).toBeInTheDocument()
        })
        it("displays validation error message given username with ':'", async () => {
            await changeInputValue("Username", "some:user")
            expect(screen.queryByText("Username can only contain letters, numbers, hyphens, and underscores")).toBeInTheDocument()
        })

        it("displays required validation error message given blank name", async () => {
            await changeInputValue("Name", "a")
            await changeInputValue("Name", "")
            expect(screen.queryByText("Name is required")).toBeInTheDocument()
        })
        it("displays validation error message given too short name", async () => {
            await changeInputValue("Name", "a".repeat(3))
            expect(screen.queryByText("It must have a minimum of 4 characters")).toBeInTheDocument()
        })
        it("displays validation error message given too long name", async () => {
            await changeInputValue("Name", "a".repeat(256))
            expect(screen.queryByText("It must have a maximum of 255 characters")).toBeInTheDocument()
        })

        it("displays required validation error message given blank email", async () => {
            await changeInputValue("Email", "a")
            await changeInputValue("Email", "")
            expect(screen.queryByText("Email is required")).toBeInTheDocument()
        })
        it("displays validation error message given invalid email address", async () => {
            await changeInputValue("Email", "some-user")
            expect(screen.queryByText("Please enter a valid email address")).toBeInTheDocument()
        })

        it("displays required validation error message given blank password", async () => {
            await changeInputValue("Password", "a")
            await changeInputValue("Password", "")
            expect(screen.queryByText("Password is required")).toBeInTheDocument()
        })
        it("displays validation error message given too short password", async () => {
            await changeInputValue("Password", "Aa1")
            expect(screen.queryByText("It must have a minimum of 8 characters")).toBeInTheDocument()
        })
        it("displays validation error message given too long password", async () => {
            await changeInputValue("Password", "Aa1".repeat(87))
            expect(screen.queryByText("It must have a maximum of 255 characters")).toBeInTheDocument()
        })
        it("displays validation error message given invalid password", async () => {
            await changeInputValue("Password", "a".repeat(8))
            expect(screen.queryByText(
                "Password must have at least one uppercase, one lowercase letter, and one number")).toBeInTheDocument()
        })

        it("displays required validation error message given blank repeat password", async () => {
            await changeInputValue("Repeat Password", "a")
            await changeInputValue("Repeat Password", "")
            expect(screen.queryByText("Please confirm password")).toBeInTheDocument()
        })
        it("displays validation error message given password mismatch", async () => {
            await changeInputValue("Repeat Password", "password")
            expect(screen.queryByText("Passwords must match")).toBeInTheDocument()
        })

        it("displays file type validation error message given invalid file format", async () => {
            const mockGifFile = new File(["file content"], "gif-file.gif", {type: "image/gif"})

            const fileInput = screen.getByLabelText("Profile Image")
            await act(() => fireEvent.change(fileInput, {target: {files: [mockGifFile]}}))

            expect(screen.queryByText("Only PNG and JPG files are allowed")).toBeInTheDocument()
        })
        it("displays file size validation error message given too large file", async () => {
            const mockPngFile = new File(["file content"], "png-file.png", {type: "image/png"})
            Object.defineProperty(mockPngFile, "size", {value: 60_000}) // 60 KB

            const fileInput = screen.getByLabelText("Profile Image")
            await act(() => fireEvent.change(fileInput, {target: {files: [mockPngFile]}}))

            expect(screen.queryByText("File size must not exceed 50 KB")).toBeInTheDocument()
        })

        it("enables submit button when form is valid", async () => {
            await setupForSubmit()
            expect(screen.queryByRole("button")).toBeEnabled()
        })
    })

    describe("Button interactions", () => {
        beforeEach(async () => {
            render(<UserSignupForm reCaptchaSiteKey="recaptcha-site-key"/>)
            await setupForSubmit()
        })

        it("displays spinner when data is loading", async () => {
            fetchMock.mockResponseOnce(
                async () => await delayedResponse("User created successfully.", 100))

            await act(() => fireEvent.click(screen.getByRole("button")))

            expect(screen.queryByText("Loading...")).toBeInTheDocument()
        })

        it("displays result when API returns valid response", async () => {
            fetchMock.mockResponseOnce("User created successfully.")

            await act(() => fireEvent.click(screen.getByRole("button")))

            expect(screen.queryByText("User created successfully.")).toBeInTheDocument()
            expect(screen.queryByRole("button", {name: "Log In"})).toBeInTheDocument()
        })

        it("displays backend validation error when user exists", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(errorResponse), {status: 400})

            await act(() => fireEvent.click(screen.getByRole("button")))

            expect(screen.queryByText("This name is in use")).toBeInTheDocument()
        })

        it("removes backend validation error when user edits username", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(errorResponse), {status: 400})

            await act(() => fireEvent.click(screen.getByRole("button")))

            expect(screen.queryByText(errorResponse.validationErrors.username)).toBeInTheDocument()

            await changeInputValue("Username", "User2")

            expect(screen.queryByText(errorResponse.validationErrors.username)).not.toBeInTheDocument()
        })

        it("displays backend validation error when reCAPTCHA error", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(errorResponse), {status: 400})

            await act(() => fireEvent.click(screen.getByRole("button")))

            expect(screen.queryByText(errorResponse.validationErrors.recaptchaToken)).toBeInTheDocument()
        })

        it("removes backend validation error when user edits reCAPTCHA", async () => {
            fetchMock.mockResponseOnce(JSON.stringify(errorResponse), {status: 400})

            await act(() => fireEvent.click(screen.getByRole("button")))

            expect(screen.queryByText(errorResponse.validationErrors.recaptchaToken)).toBeInTheDocument()

            await act(() => fireEvent.click(screen.getByTestId("recaptcha-checkbox")))

            expect(screen.queryByText(errorResponse.validationErrors.recaptchaToken)).not.toBeInTheDocument()
        })
    })
})