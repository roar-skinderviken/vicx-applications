import {render, screen} from "@testing-library/react";
import Home from "@/app/page";
import {SITE_PAGES} from "@/constants/sitePages";

describe("Home Page", () => {
    describe("Layout", () => {

        beforeEach(() => render(Home()))

        it("displays lead text", () => {
            expect(screen.queryByText("Explore my showcase of recent work")).toBeInTheDocument()
        })

        it("displays Table of Contents heading ", () => {
            expect(screen.queryByRole("heading", {name: "Table of Contents", level: 2})).toBeInTheDocument()
        })

        it("displays page links", () => {
            SITE_PAGES.forEach(({href}, index) => {
                const link = screen.queryByTestId(`page-link-${index}`)
                expect(link).toHaveAttribute("href", href)
            })
        })

        it("displays page titles", () => {
            SITE_PAGES.forEach(({title}, index) => {
                const link = screen.queryByTestId(`page-link-${index}`)
                expect(link).toHaveTextContent(title)
            })
        })

        it("displays page images", () => {
            SITE_PAGES.forEach(({imgAlt}) => {
                expect(screen.queryByAltText(imgAlt)).toBeInTheDocument()
            })
        })
    })
})