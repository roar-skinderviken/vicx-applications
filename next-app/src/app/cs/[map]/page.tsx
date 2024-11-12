import SelectSmokePlace from "@/app/cs/[map]/SelectSmokePlace"
import {notFound} from "next/navigation"
import Hero from "@/components/Hero"
import Image from "next/image"
import {MAPS} from "@/constants/mapEntries"

import csBackgroundImage from "@/assets/images/cs2back.jpg"

export const dynamicParams = false
export const revalidate = 3600

export async function generateMetadata({params}: { params: Promise<{ map: string }> }) {
    const {map} = await params
    const selectedMap = MAPS.find((currentMap) => currentMap.name === map)

    if (!selectedMap) {
        return {
            title: "Map Not Found | VICX",
            description: "The specified map could not be found. Check out other Counter Strike smokes.",
        }
    }

    return {
        title: `${selectedMap.name} Smoke Places | VICX`,
        description: `Discover the best smoke places for ${selectedMap.name} in Counter Strike.`,
    }
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
                backgroundImage={csBackgroundImage}
            />

            <div className="flex flex-col items-center">
                <Image
                    src={selectedMap.image}
                    className="mt-4 w-32"
                    alt={selectedMap.name}/>
                <h2 className="text-center text-2xl my-2">Choose Your Side for {selectedMap.name}</h2>
            </div>
            <SelectSmokePlace selectedMap={selectedMap}/>
        </main>
    )
}

export default SmokesPage