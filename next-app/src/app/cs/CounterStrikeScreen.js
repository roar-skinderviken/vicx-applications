"use client"

import {useState} from "react"

const MAPS = [
    {
        name: "Mirage",
        image: "mirage.png",
        ct: ["Palace", "Apartments", "Top Mid"],
        t: ["Ticket", "Stairs", "Jungle"]
    },
    {
        name: "Dust2",
        image: "dust2.png",
        ct: [],
        t: ["Entrance", "B Window", "B Door"]
    },
    {
        name: "Anubis",
        image: "anubis.png",
        ct: ["Top Mid", "Connector"],
        t: ["Window"]
    },
    {
        name: "Vertigo",
        image: "vertigo.png",
        ct: ["B Default"],
        t: ["CT Spawn", "Guardian", "Elevator", "Elevator A"]
    },
    {
        name: "Ancient",
        image: "ancient.png",
        ct: ["CT A", "Temple", "Pyramid"],
        t: ["Mid", "A Site", "Donut"]
    },
    {
        name: "Nuke",
        image: "nuke.png",
        ct: ["Under Silo", "T Red", "Trophy"],
        t: ["Ramp", "Trophy", "A Vent"]
    },
    {
        name: "Inferno",
        image: "inferno.png",
        ct: [],
        t: ["Banana", "Coffins"]
    },
]

const createImagePath = (mapName, side, smokePlace) => {
    const cleanString = (str) => str.replace(/\s+/g, '-').toLowerCase()
    return `/images/smoke-places/${cleanString(mapName)}-${side}-${cleanString(smokePlace)}.jpg`
}

const CounterStrikeScreen = () => {
    const [selectedMapName, setSelectedMapName] = useState()
    const [selectedSmokePlaces, setSelectedSmokePlaces] = useState({})
    const [selectedSmokePlace, setSelectedSmokePlace] = useState("")

    const reset = () => {
        setSelectedMapName(undefined)
        setSelectedSmokePlaces({})
        setSelectedSmokePlace(undefined)
    }

    const displayAvailableMaps = () =>
        <div>
            <h2 className="text-center text-3xl my-4">Available Maps</h2>
            <div className="container mx-auto grid grid-cols-1 sm:grid-cols-4 gap-4 cursor-pointer ">
                {MAPS.map((map, index) => (
                    <div
                        key={index}
                        onClick={() => setSelectedMapName(map.name)}
                        className="flex flex-col items-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg rounded-lg cursor-pointer"
                    >
                        <img
                            src={`/images/${map.image}`}
                            className="w-32"
                            alt={map.name}/>
                        <span className="my-2 text-center">{map.name}</span>
                    </div>
                ))}
            </div>
        </div>

    if (!selectedMapName) {
        return displayAvailableMaps()
    }

    const selectedMap = MAPS.find(map => map.name === selectedMapName)

    return (
        <div className="flex flex-col items-center">
            <h2 className="text-center text-3xl my-4">Choose Your Side for {selectedMap.name}</h2>
            <img
                src={`/images/${selectedMap.image}`}
                className="w-32"
                alt={selectedMap.name}/>

            <div className="flex space-x-4 mt-4">
                <button
                    className="cs-side-button bg-blue-500 hover:bg-blue-600"
                    disabled={selectedMap.ct.length < 1}
                    onClick={() => {
                        setSelectedSmokePlace(selectedMap.ct[0])
                        setSelectedSmokePlaces({side: "ct", places: selectedMap.ct})
                    }}>
                    C-Side
                </button>

                <button
                    className="cs-side-button bg-red-500 hover:bg-red-600"
                    disabled={selectedMap.t.length < 1}
                    onClick={() => {
                        setSelectedSmokePlace(selectedMap.t[0])
                        setSelectedSmokePlaces({side: "t", places: selectedMap.t})
                    }}>
                    T-Side
                </button>

                <button
                    className="bg-gray-200 text-gray-700 py-2 px-4 rounded shadow hover:bg-gray-300 hover:shadow-md"
                    onClick={() => reset()}>
                    Back to Map Selector
                </button>
            </div>

            {selectedSmokePlaces && selectedSmokePlaces.side &&
                <div className="flex flex-col items-center mt-5">
                    <h2 className="text-center text-3xl my-4">Smoke Places {selectedSmokePlaces.side === "ct" ? "C-Side" : "T-Side"}</h2>

                    {/* Tabs */}
                    <div
                        className="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700">
                        <ul className="flex flex-wrap -mb-px">
                            {selectedSmokePlaces.places.map((smokePlace, index) => (
                                <li className="me-2" key={index}>
                                    <button
                                        className={selectedSmokePlace === smokePlace ? "cs-active-tab" : "cs-inactive-tab"}
                                        aria-current="page"
                                        onClick={() => setSelectedSmokePlace(smokePlace)}>
                                        {smokePlace}
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </div>

                    {/* Image Display Area */}
                    {selectedSmokePlace && (
                        <div className="w-full p-4 border rounded-b-lg border-gray-300 bg-gray-100">
                            <img
                                src={createImagePath(selectedMap.name, selectedSmokePlaces.side, selectedSmokePlace)}
                                className="my-2 max-w-6xl"
                                alt={selectedSmokePlace}
                            />
                        </div>
                    )}
                </div>
            }
        </div>
    )
}

export default CounterStrikeScreen
