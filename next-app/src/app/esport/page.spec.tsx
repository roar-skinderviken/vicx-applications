import {render, screen} from "@testing-library/react"
import EsportPage from "@/app/esport/page"
import {PANDASCORE_BASE_URL} from "@/constants/pandascoreConstants"

jest.mock('next/cache', () => ({
    revalidateTag: jest.fn(),
}))

const runningMatches = [{
    begin_at: "01/01/2024",
    status: "running",
    opponents: [{opponent: {name: "Team-1"}}, {opponent: {name: "Team-2"}}]
}]

const upcomingMatches = [{
    begin_at: "02/02/2024",
    status: "not_started",
    opponents: [{opponent: {name: "Team-3"}}, {opponent: {name: "Team-4"}}]
}]

const runningMatchesUrl = `${PANDASCORE_BASE_URL}/running?token=`
const upcomingMatchesUrl = `${PANDASCORE_BASE_URL}/running?token=`

describe("Esport Page", () => {
    describe("Layout", () => {
        it("displays header Running Matches", async () => {
            fetchMock.mockResponse("", {status: 500})
            render(await EsportPage())
            expect(screen.queryByText("Running Matches")).toBeInTheDocument()
        })

        it("displays header Upcoming Matches", async () => {
            fetchMock.mockResponse("", {status: 500})
            render(await EsportPage())
            expect(screen.queryByText("Upcoming Matches")).toBeInTheDocument()
        })

        it("displays running matches when matches are returned by fetch", async () => {
            fetchMock
                .mockResponseOnce(JSON.stringify(runningMatches), {url: runningMatchesUrl})
                .mockResponseOnce(JSON.stringify([]), {url: upcomingMatchesUrl})

            render(await EsportPage())

            expect(screen.queryByText("Team-1 vs Team-2")).toBeInTheDocument()
            expect(screen.queryByText("running")).toBeInTheDocument()
            expect(screen.queryByText("1/1/2024")).toBeInTheDocument()
        })

        it("displays upcoming matches when matches are returned by fetch", async () => {
            fetchMock
                .mockResponseOnce(JSON.stringify(upcomingMatches), {url: upcomingMatchesUrl})
                .mockResponseOnce(JSON.stringify([]), {url: runningMatchesUrl})

            render(await EsportPage())

            expect(screen.queryByText("Team-3 vs Team-4")).toBeInTheDocument()
            expect(screen.queryByText("not_started")).toBeInTheDocument()
            expect(screen.queryByText("2/2/2024")).toBeInTheDocument()
        })
    })
})