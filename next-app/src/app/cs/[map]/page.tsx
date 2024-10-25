import SelectSmokePlace from "@/app/cs/[map]/SelectSmokePlace"
import {notFound} from "next/navigation"
import {MAPS} from "@/app/cs/mapEntries"
import Hero from "@/components/Hero"

export const dynamicParams = false
export const revalidate = 3600

export const metadata = {
    title: "Counter Strike Smokes | VICX"
}

export async function generateStaticParams() {
    return MAPS.map(({name}) => ({
        map: name
    }))
}

const SmokesPage = async ({params}: { params: Promise<{ map: string }> }) => {
    const {map} = await params
    const selectedMap = MAPS.find(currentMap => currentMap.name === map)

    if (!selectedMap) {
        notFound()
    }

    return (
        <main className="content">
            <Hero
                title="Counter Strike Smokes"
                lead={`${selectedMap.name} Smoke Places`}
                backgroundImage="/images/cs2back.jpg"
            />

            <div>
                <div className="flex flex-col items-center mb-4">
                    <img
                        src={`/images/${selectedMap.image}`}
                        className="w-32 mt-4"
                        alt={selectedMap.name}/>
                    <h2 className="text-center text-3xl my-4">Choose Your Side for {selectedMap.name}</h2>
                    <SelectSmokePlace selectedMap={selectedMap}/>
                </div>
            </div>
        </main>
    )
}

export default SmokesPage