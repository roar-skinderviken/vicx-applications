import {act, fireEvent, render, screen} from "@testing-library/react"
import {usePathname} from "next/navigation"
import {SITE_PAGES} from "@/constants/sitePages"
import VicxNavbar from "@/components/VicxNavbar"
import {signIn, signOut, useSession} from "next-auth/react"

jest.mock("next/navigation", () => ({
    usePathname: jest.fn()
}))

jest.mock("next-auth/react", () => ({
    useSession: jest.fn(),
    signIn: jest.fn(),
    signOut: jest.fn()
}))

const mockUsePathname = usePathname as jest.Mock
const mockUseSession = useSession as jest.Mock

describe('VicxNavbar', () => {

    describe("Layout", () => {
        beforeEach(() => {
            jest.clearAllMocks()
            mockUsePathname.mockReturnValue('/')
            mockUseSession.mockReturnValue({
                data: {},
                status: "unauthenticated"
            })
        })

        it("Displays brand in navbar", () => {
            render(<VicxNavbar/>)
            expect(screen.queryByText("VICX")).toBeInTheDocument()
        })

        it("Displays signin link in navbar when user is not logged in", () => {
            render(<VicxNavbar/>)
            expect(screen.queryByText("Sign in")).toBeInTheDocument()
        })

        it("Displays username in navbar when user is logged in", () => {
            mockUseSession.mockReturnValue({
                data: {user: {name: "user1"}},
                status: "authenticated"
            })

            render(<VicxNavbar/>)
            expect(screen.queryByText("user1")).toBeInTheDocument()
        })

        it.each(SITE_PAGES.map(({href, title}) => [href, title]))(
            "Displays highlighted page link for pathname '%s' with title '%s'",
            (href, title) => {
                mockUsePathname.mockReturnValue(href)

                render(<VicxNavbar/>)

                const pageLink = screen.queryByText(title)
                expect(pageLink).toBeInTheDocument()
                expect(pageLink).toHaveClass("dark:text-white")
            }
        )
    })

    describe("Interactions", () => {
        beforeEach(() => mockUsePathname.mockReturnValue('/'))

        it("calls signIn when signIn link is clicked", () => {
            mockUseSession.mockReturnValue({
                data: {user: {name: "user1"}},
                status: "unauthenticated"
            })

            render(<VicxNavbar/>)

            fireEvent.click(screen.getByText("Sign in"))

            expect(signIn).toHaveBeenCalled()
        })

        it("expands user dropdown when username is clicked", async () => {
            mockUseSession.mockReturnValue({
                data: {user: {name: "user1"}},
                status: "authenticated"
            })

            render(<VicxNavbar/>)

            await act(async () => fireEvent.click(screen.getByText("user1")))

            expect(screen.queryByText("Dashboard")).toBeInTheDocument()
            expect(screen.queryByText("Sign out")).toBeInTheDocument()
        })

        it("calls signOut when signOut is clicked", async () => {
            mockUseSession.mockReturnValue({
                data: {user: {name: "user1"}},
                status: "authenticated"
            })

            render(<VicxNavbar/>)

            await act(async () => fireEvent.click(screen.getByText("user1")))
            await act(async () => fireEvent.click(screen.getByText("Sign out")))

            expect(signOut).toHaveBeenCalled()
        })
    })
})
