import Hero from "@/components/Hero"

export interface MatchEntry {
    id: number,
    name: string,
    begin_at: string,
    status: string
}

const SPRING_BACKEND_BASE_URL = process.env.SPRING_BACKEND_BASE_URL || ""
const ESPORT_URL = SPRING_BACKEND_BASE_URL + "/api/esport"

const getMatches = async () => {
    try {
        const response = await fetch(ESPORT_URL)

        if (!response.ok) return []

        return await response.json()
    } catch (error) {
        console.error(error)
        return []
    }
}

const displayMatches = (matches: MatchEntry[]) =>
    matches.map(match =>
        <div key={match.id} className="border p-4 mb-4 rounded-lg shadow-lg bg-white">
            <h2 className="text-xl font-bold mb-2">{match.name}</h2>
            <p className="text-lg">
                <span className="text-blue-600 px-1 rounded">Date:</span>
                {new Date(match.begin_at).toLocaleDateString()}
            </p>
            <p className="text-lg">
                <span className="text-blue-600 px-1 rounded">Status:</span>
                {match.status}
            </p>
        </div>)

export const dynamic = "force-dynamic"

export const metadata = {
    title: "Esport | VICX"
}

export default async function EsportPage() {
    const matches = await getMatches()

    return <main className="content">
        <Hero
            title="Counter Strike Esport"
            lead="These are the live matches"
        />

        <div className="container mx-auto grid grid-cols-1 sm:grid-cols-2 gap-4 px-2">
            <div>
                <h2 className="text-center text-3xl my-4">Running Matches</h2>
                {displayMatches(matches.runningMatches)}
            </div>

            <div>
                <h2 className="text-center text-3xl my-4">Upcoming Matches</h2>
                {displayMatches(matches.upcomingMatches)}
            </div>
        </div>
    </main>
}