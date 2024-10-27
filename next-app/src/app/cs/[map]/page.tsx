import SelectSmokePlace from "@/app/cs/[map]/SelectSmokePlace"
import {notFound} from "next/navigation"
import Hero from "@/components/Hero"
import Image from "next/image"
import {MAPS} from "@/constants/mapEntries"

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
        return
    }

    return (
        <main className="content">
            <Hero
                title="Counter Strike Smokes"
                lead={`${selectedMap.name} Smoke Places`}
                backgroundImage="/images/cs2back.jpg"
            />

            <div className="flex flex-col items-center">
                <Image
                    src={`/images/${selectedMap.image}`}
                    className="mt-4"
                    alt={selectedMap.name}
                    width="128" height="128"/>
                <h2 className="text-center text-2xl my-2">Choose Your Side for {selectedMap.name}</h2>
            </div>
            <SelectSmokePlace selectedMap={selectedMap}/>
        </main>
    )
}

export default SmokesPage