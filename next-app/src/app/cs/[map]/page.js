import {MAPS} from "@/app/cs/mapConstants"
import SelectSmokePlace from "@/app/cs/[map]/SelectSmokePlace"
import {notFound} from "next/navigation"

export const dynamic = "force-static"

export default function SmokesPage({params}) {

    const {map} = params

    const selectedMap = MAPS.find(currentMap => currentMap.name === map)

    if (!selectedMap) {
        notFound()
        return
    }

    return (
        <main className="content">
            <div className="hero cs-hero">
                <h1><span>Counter Strike Smokes</span></h1>
                <p className="lead">{selectedMap.name} Smoke Places</p>
            </div>
            <div>
                <div className="flex flex-col items-center">
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
