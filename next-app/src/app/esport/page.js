// noinspection JSUnresolvedReference

import Hero from "@/components/Hero"
import {revalidateTag} from "next/cache"

const PANDASCORE_BASE_URL = "https://api.pandascore.co/csgo/matches"
const RUNNING_MATCH_TYPE = "running"
const UPCOMING_MATCH_TYPE = "upcoming"
const CACHE_TIMEOUT_IN_SECS = 30
const CACHE_TAG_BASE = "pandascore-cache-"

const apiKey = process.env.API_KEY

const getMatches = async (matchType) => {
    const cacheTag = `${CACHE_TAG_BASE}${matchType}`

    try {
        const response = await fetch(
            `${PANDASCORE_BASE_URL}/${matchType}?token=${apiKey}`,
            {
                cache: "force-cache",
                next: {
                    revalidate: CACHE_TIMEOUT_IN_SECS,
                    tags: [cacheTag]
                }
            }
        )

        if (!response.ok) {
            await revalidateTag(cacheTag)
            return []
        }

        const data = await response.json()
        return data.filter(match => match.opponents && match.opponents.length === 2)
    } catch (error) {
        return []
    }
}

const displayMatches = (matches) =>
    matches.map((match, index) => {
        const opponent1 = match.opponents[0].opponent.name
        const opponent2 = match.opponents[1].opponent.name

        return (
            <div key={index} className="border p-4 mb-4 rounded-lg shadow-lg bg-white">
                <h2 className="text-xl font-bold mb-2">{opponent1} vs {opponent2}</h2>
                <p className="text-lg">
                    <span className="text-blue-600 px-1 rounded">Date:</span>
                    {new Date(match.begin_at).toLocaleDateString()}
                </p>
                <p className="text-lg">
                    <span className="text-blue-600 px-1 rounded">Status:</span>
                    {match.status}
                </p>
            </div>
        )
    })

export const metadata = {
    title: "Esport | VICX"
}

export default async function EsportPage() {
    const [runningMatches, upcomingMatches] = await Promise.all([
        getMatches(RUNNING_MATCH_TYPE),
        getMatches(UPCOMING_MATCH_TYPE)
    ])

    return <main className="content">
        <Hero
            title="Counter Strike Esport"
            lead="These are the live matches"
        />

        <div className="container mx-auto grid grid-cols-1 sm:grid-cols-2 gap-4 px-2">
            <div>
                <h2 className="text-center text-3xl my-4">Running Matches</h2>
                {displayMatches(runningMatches)}
            </div>

            <div>
                <h2 className="text-center text-3xl my-4">Upcoming Matches</h2>
                {displayMatches(upcomingMatches)}
            </div>
        </div>
    </main>
}
