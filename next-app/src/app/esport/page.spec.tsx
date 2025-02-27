import {render, screen} from "@testing-library/react"
import EsportPage from "@/app/esport/page"

jest.mock('next/cache', () => ({
    revalidateTag: jest.fn(),
}))

const runningMatches = [{
    id: 41,
    name: "Team-1 vs Team-2",
    begin_at: "01/01/2024",
    status: "running"
}]

const upcomingMatches = [{
    id: 42,
    name: "Team-3 vs Team-4",
    begin_at: "02/02/2024",
    status: "not_started"
}]

const ESPORT_URL = "/backend-spring-boot/api/esport"

describe("Esport Page", () => {
    describe("Layout", () => {
        it("displays running matches when matches are returned by fetch", async () => {
            fetchMock
                .mockResponseOnce(JSON.stringify({runningMatches, upcomingMatches: []}), {url: ESPORT_URL})

            render(await EsportPage())

            expect(screen.queryByText("Team-1 vs Team-2")).toBeInTheDocument()
            expect(screen.queryByText("running")).toBeInTheDocument()
            expect(screen.queryByText("1/1/2024")).toBeInTheDocument()
        })

        it("displays upcoming matches when matches are returned by fetch", async () => {
            fetchMock
                .mockResponseOnce(JSON.stringify({runningMatches: [], upcomingMatches}), {url: ESPORT_URL})

            render(await EsportPage())

            expect(screen.queryByText("Team-3 vs Team-4")).toBeInTheDocument()
            expect(screen.queryByText("not_started")).toBeInTheDocument()
            expect(screen.queryByText("2/2/2024")).toBeInTheDocument()
        })
    })
})