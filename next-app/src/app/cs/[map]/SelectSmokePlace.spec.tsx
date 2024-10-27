import {fireEvent, render, screen} from "@testing-library/react"
import SelectSmokePlace from "@/app/cs/[map]/SelectSmokePlace"
import {MAPS} from "@/constants/mapEntries";

const SELECTED_MAP = MAPS[0]

const testSideInteractions = (side: "C-Side" | "T-Side", entries: string[]) => {
    describe(`${side} Interactions`, () => {
        beforeEach(() => {
            render(<SelectSmokePlace selectedMap={SELECTED_MAP}/>)
            fireEvent.click(screen.getByText(side))
        })

        it(`displays ${side} Smoke Places header when ${side} button is clicked`, () => {
            expect(screen.queryByText(`${side} Smoke Places`)).toBeInTheDocument()
        })

        it(`displays ${side} Smoke Places in tabs`, () => {
            entries.forEach(entry =>
                expect(screen.queryByText(entry)).toBeInTheDocument()
            )
        })

        it(`displays first smoke place image for ${side}`, () => {
            expect(screen.queryByAltText(entries[0])).toBeInTheDocument()
        })

        it(`displays second smoke place image for ${side} when second tab is clicked`, () => {
            const secondTab = screen.getByRole("tab", {name: entries[1]})
            fireEvent.click(secondTab)
            expect(screen.queryByAltText(entries[1])).toBeInTheDocument()
        })
    })
}

describe('SelectSmokePlace', () => {
    describe('Layout', () => {
        it("displays disabled C-Side button when no C-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={{...SELECTED_MAP, ct: []}}/>)

            const cSideButton = screen.queryByText("C-Side")
            expect(cSideButton).toBeInTheDocument()
            expect(cSideButton).toBeDisabled()
        })

        it("displays enabled C-Side button when there are C-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={SELECTED_MAP}/>)
            expect(screen.getByText("C-Side")).toBeEnabled()
        })

        it("displays disabled T-Side button when no T-Side smoke places", () => {
            render(<SelectSmokePlace selectedMap={{...SELECTED_MAP, t: []}}/>)

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
        testSideInteractions("C-Side", SELECTED_MAP.ct)
        testSideInteractions("T-Side", SELECTED_MAP.t)
    })
})