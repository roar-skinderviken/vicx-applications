import {render, screen} from "@testing-library/react"
import {usePathname} from "next/navigation"
import {SITE_PAGES} from "@/constants/sitePages";
import VicxNavbar from "@/components/VicxNavbar";

// Mock the usePathname hook
jest.mock("next/navigation", () => ({
    usePathname: jest.fn(),
}))

const mockUsePathname = usePathname as jest.Mock

describe('VicxNavbar', () => {

    describe("Layout", () => {
        beforeEach(() => jest.clearAllMocks())

        it("Displays brand in navbar", () => {
            mockUsePathname.mockReturnValue('/')

            render(<VicxNavbar/>)

            expect(screen.queryByText("VICX")).toBeInTheDocument()
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
})
