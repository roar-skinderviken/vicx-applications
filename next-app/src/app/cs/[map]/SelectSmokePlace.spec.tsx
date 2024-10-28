import {fireEvent, render, screen} from "@testing-library/react"
import SelectSmokePlace from "@/app/cs/[map]/SelectSmokePlace"
import {MAPS, MapSide, SmokePlace} from "@/constants/mapEntries";

const SELECTED_MAP = MAPS[0]

const testSideInteractions = (side: MapSide, smokePlaces: SmokePlace[]) => {
    describe(`${side}-Side Interactions`, () => {
        beforeEach(() => {
            render(<SelectSmokePlace selectedMap={SELECTED_MAP}/>)
            fireEvent.click(screen.getByText(`${side}-Side`))
        })

        it(`displays ${side}-Side Smoke Places header when ${side}-Side button is clicked`, () => {
            expect(screen.queryByText(`${side}-Side Smoke Places`)).toBeInTheDocument()
        })

        it(`displays ${side}-Side Smoke Places in tabs`, () => {
            smokePlaces.forEach(({name}) =>
                expect(screen.queryByText(name)).toBeInTheDocument()
            )
        })

        it(`displays first smoke place image for ${side}-Side`, () => {
            expect(screen.queryByAltText(smokePlaces[0].name)).toBeInTheDocument()
        })

        it(`displays second smoke place image for ${side}-Side when second tab is clicked`, () => {
            const secondTab = screen.getByRole("tab", {name: smokePlaces[1].name})
            fireEvent.click(secondTab)
            expect(screen.queryByAltText(smokePlaces[1].name)).toBeInTheDocument()
        })
    })
}

describe('SelectSmokePlace', () => {
    describe('Layout', () => {
        it("displays disabled C-Side button when no C-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={{...SELECTED_MAP, cSide: []}}/>)

            const cSideButton = screen.queryByText("C-Side")
            expect(cSideButton).toBeInTheDocument()
            expect(cSideButton).toBeDisabled()
        })

        it("displays enabled C-Side button when there are C-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={SELECTED_MAP}/>)
            expect(screen.getByText("C-Side")).toBeEnabled()
        })

        it("displays disabled T-Side button when no T-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={{...SELECTED_MAP, tSide: []}}/>)

            const tSideButton = screen.queryByText("T-Side")
            expect(tSideButton).toBeInTheDocument()
            expect(tSideButton).toBeDisabled()
        })

        it("displays enabled T-Side button when there are T-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={SELECTED_MAP}/>)
            expect(screen.getByText("T-Side")).toBeEnabled()
        })

        it("displays 'Back to Map Selector link'", () => {
            render(<SelectSmokePlace selectedMap={SELECTED_MAP}/>)

            const link = screen.queryByText("Back to Map Selector")

            expect(link).toBeInTheDocument()
            expect(link).toHaveAttribute("href", "/cs#maps")
        })
    })

    describe("Interactions", () => {
        testSideInteractions("C", SELECTED_MAP.cSide)
        testSideInteractions("T", SELECTED_MAP.tSide)
    })
})