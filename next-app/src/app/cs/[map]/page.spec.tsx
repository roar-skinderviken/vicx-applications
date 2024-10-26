import {render, screen} from "@testing-library/react"
import SmokesPage from "@/app/cs/[map]/page"
import {notFound} from "next/navigation"

jest.mock("next/navigation", () => ({
    notFound: jest.fn()
}))

describe("SmokesPage", () => {
    describe("Layout", () => {
        const SELECTED_MAP = "Mirage"

        beforeEach(async () =>
            render(await SmokesPage({params: Promise.resolve({map: SELECTED_MAP})}))
        )

        it("displays map name in Hero", async () => {
            expect(screen.queryByText(`${SELECTED_MAP} Smoke Places`)).toBeInTheDocument()
        })

        it("displays map image", async () => {
            const image = screen.queryByAltText(SELECTED_MAP)
            expect(image).toBeInTheDocument()
            expect(image).toHaveAttribute("src", expect.stringContaining(SELECTED_MAP.toLowerCase()))
        })

        it("displays 'Choose Your Side' text for selected map", async () => {
            expect(screen.queryByText(`Choose Your Side for ${SELECTED_MAP}`)).toBeInTheDocument()
        })
    })

    describe("Routing", () => {
        it("calls notFound() when an invalid map is provided", async () => {
            render(await SmokesPage({params: Promise.resolve({map: "invalidMap"})}))
            expect(notFound).toHaveBeenCalled()
        })
    })
})
