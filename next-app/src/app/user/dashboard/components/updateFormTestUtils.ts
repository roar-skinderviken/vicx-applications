import {getSession, signOut} from "next-auth/react"
import {CustomSession} from "@/types/authTypes"
import {REFRESH_ACCESS_TOKEN_ERROR} from "@/auth/tokenUtils"
import {act, fireEvent, screen, waitFor} from "@testing-library/react"
import {delayedResponse} from "@/testUtils"

export const mockGetSession = getSession as jest.Mock
export const mockSignOut = signOut as jest.Mock

export const sessionUser = (roles: string[]) => ({
    id: "user1",
    name: "Jon Doe",
    roles: roles
})

export const validSession: CustomSession = {
    expires: Date.now().toString(),
    user: sessionUser(["ROLE_USER"])
}

export const sessionWithTokenError: CustomSession = {
    ...validSession,
    error: REFRESH_ACCESS_TOKEN_ERROR
}

export const errorResponse = {
    validationErrors: {
        name: "Something went wrong",
        email: "Something went wrong",
        currentPassword: "Something went wrong"
    }
}

export const mockOmEndEdit = jest.fn()

export const displaysBackendValidationErrorTest = async (buttonName: string = "Save") => {
    mockGetSession.mockResolvedValueOnce(validSession)
    fetchMock.mockResponseOnce(JSON.stringify(errorResponse), {status: 400})

    await act(() => fireEvent.click(screen.getByRole("button", {name: buttonName})))

    await waitFor(() => {
        expect(screen.queryByText("Something went wrong")).toBeInTheDocument()
    })
}

export const displaysSpinnerTest = async (buttonName: string = "Save") => {
    mockGetSession.mockResolvedValue(validSession)

    fetchMock.mockResponseOnce(
        async () => await delayedResponse("User updated successfully.", 100))

    const submitButton = screen.getByRole("button", {name: buttonName})
    await act(() => fireEvent.click(submitButton))

    expect(submitButton).toHaveTextContent("Loading...")
}

export const userLogoutTest = async (buttonName: string = "Save") => {
    mockGetSession.mockResolvedValueOnce(sessionWithTokenError)

    await act(() => fireEvent.click(screen.getByRole("button", {name: buttonName})))

    expect(mockSignOut).toHaveBeenCalledWith({
        callbackUrl: "/user/signed-out",
        redirect: true
    })
}

export const callsOnEndEditTest = () => {
    fireEvent.click(screen.getByRole("button", {name: "Close"}))
    expect(mockOmEndEdit).toHaveBeenCalled()
}