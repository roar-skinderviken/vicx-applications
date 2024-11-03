import {act, fireEvent, render, screen} from "@testing-library/react"
import {usePathname} from "next/navigation"
import {SITE_PAGES} from "@/constants/sitePages"
import VicxNavbar from "@/components/navbar/VicxNavbar"
import {signIn, signOut} from "next-auth/react"
import {useSessionUser} from "@/components/navbar/useSessionUser"

jest.mock("next/navigation", () => ({
    usePathname: jest.fn()
}))

jest.mock("next-auth/react", () => ({
    signIn: jest.fn(),
    signOut: jest.fn()
}))

jest.mock("@/components/navbar/useSessionUser", () => ({
    useSessionUser: jest.fn(),
}))

const mockUsePathname = usePathname as jest.Mock
const mockUseSessionUser = useSessionUser as jest.Mock

const setupUnauthenticated = () => {
    mockUseSessionUser.mockReturnValue({})
    render(<VicxNavbar/>)
}

const setupAuthenticated = (email?: string, image?: string) => {
    mockUseSessionUser.mockReturnValue({name: "user1", email: email, image: image})
    render(<VicxNavbar/>)
}

describe('VicxNavbar', () => {

    describe("Layout", () => {
        beforeEach(() => {
            jest.clearAllMocks()
            mockUsePathname.mockReturnValue('/')
        })

        it("Displays brand in navbar", () => {
            setupUnauthenticated()
            expect(screen.queryByText("VICX")).toBeInTheDocument()
        })

        it("Displays signin link in navbar when user is not logged in", () => {
            setupUnauthenticated()
            expect(screen.queryByText("Sign in")).toBeInTheDocument()
        })

        it("Displays fallback avatar in navbar when user is logged in and image is not provided", () => {
            setupAuthenticated()

            const avatarImage = screen.queryByAltText("User settings")
            expect(avatarImage).toBeInTheDocument()
            expect(avatarImage).toHaveAttribute("src", "/img.jpg")
        })

        it("Displays avatar in navbar when user is logged in and image is provided", () => {
            setupAuthenticated(undefined, "/some-avatar.png")

            const avatarImage = screen.queryByAltText("User settings")
            expect(avatarImage).toHaveAttribute("src", "/some-avatar.png")
        })

        it.each(SITE_PAGES.map(({href, title}) => [href, title]))(
            "Displays highlighted page link for pathname '%s' with title '%s'",
            (href, title) => {
                mockUsePathname.mockReturnValue(href)

                setupUnauthenticated()

                const pageLink = screen.queryByText(title)
                expect(pageLink).toBeInTheDocument()
                expect(pageLink).toHaveClass("dark:text-white")
            }
        )
    })

    describe("Interactions", () => {
        beforeEach(() => mockUsePathname.mockReturnValue('/'))

        it("calls signIn when signIn link is clicked", () => {
            setupUnauthenticated()

            fireEvent.click(screen.getByText("Sign in"))

            expect(signIn).toHaveBeenCalled()
        })

        it("expands user dropdown with userinfo when avatar is clicked", async () => {
            setupAuthenticated()

            await act(async () => fireEvent.click(screen.getByTestId("flowbite-avatar")))

            expect(screen.queryByText("user1")).toBeInTheDocument()
            expect(screen.queryByText("Dashboard")).toBeInTheDocument()
            expect(screen.queryByText("Sign out")).toBeInTheDocument()
        })

        it("displays email address in dropdown when user has email", async () => {
            setupAuthenticated("user1@example.com")

            await act(async () => fireEvent.click(screen.getByTestId("flowbite-avatar")))

            expect(screen.queryByText("user1@example.com")).toBeInTheDocument()
        })

        it("calls signOut when signOut is clicked", async () => {
            setupAuthenticated()

            await act(async () => fireEvent.click(screen.getByTestId("flowbite-avatar")))
            await act(async () => fireEvent.click(screen.getByText("Sign out")))

            expect(signOut).toHaveBeenCalled()
        })
    })
})
