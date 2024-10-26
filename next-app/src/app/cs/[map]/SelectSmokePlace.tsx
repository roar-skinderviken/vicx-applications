"use client"

import {useState, useCallback} from "react"
import Image from "next/image"
import Link from "next/link"
import {CsMapEntry} from "@/constants/mapEntries";

const INITIAL_SMOKE_PLACES = {side: "", places: [""]}

const createImagePath = (mapName: string, side: string, smokePlace: string) => {
    const cleanString = (str: string) => str.replace(/\s+/g, '-').toLowerCase()
    return `/images/smoke-places/${cleanString(mapName)}-${side}-${cleanString(smokePlace)}.jpg`
}

const SelectSmokePlace = ({selectedMap}: { selectedMap: CsMapEntry }) => {
    const {ct, t} = selectedMap
    const [smokePlaces, setSmokePlaces] = useState(INITIAL_SMOKE_PLACES)
    const [selectedSmokePlace, setSelectedSmokePlace] = useState("")

    const handleClick = useCallback((side: string, places: string[]) => {
        setSelectedSmokePlace(places[0])
        setSmokePlaces({side, places})
    }, [])

    return (
        <div className="flex flex-col items-center w-full px-4">
            {/* Button row */}
            <div className="flex flex-nowrap space-x-4 mt-4 overflow-x-auto">
                <button
                    className="cs-side-button bg-blue-500 hover:bg-blue-600 whitespace-nowrap"
                    disabled={ct.length < 1}
                    onClick={() => handleClick("ct", ct)}>
                    C-Side
                </button>

                <button
                    className="cs-side-button bg-red-500 hover:bg-red-600 whitespace-nowrap"
                    disabled={t.length < 1}
                    onClick={() => handleClick("t", t)}>
                    T-Side
                </button>

                <Link href="/cs#maps"
                      className="bg-gray-200 text-gray-700 py-2 px-4 rounded shadow hover:bg-gray-300 hover:shadow-md whitespace-nowrap">
                    Back to Map Selector
                </Link>
            </div>

            {smokePlaces.side && ( // Simplified condition
                <div className="flex flex-col items-center mt-5 w-full">
                    <h2 className="text-center text-3xl my-4">
                        {smokePlaces.side === "ct" ? "C-Side" : "T-Side"} Smoke Places
                    </h2>

                    {/* Tabs */}
                    <div className="flex justify-center w-full">
                        <div
                            className="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700 w-full">
                            <ul className="flex flex-wrap justify-center">
                                {smokePlaces.places.map((smokePlace) => ( // Removed index for keys
                                    <li className="mr-2" key={smokePlace}>
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
                    </div>

                    {/* Image Display Area */}
                    {selectedSmokePlace && (
                        <div className="w-full p-4 border rounded-b-lg border-gray-300 bg-gray-100">
                            <div
                                className="relative w-full mx-auto h-[300px] sm:h-[500px] lg:h-[1000px] overflow-hidden">
                                <Image
                                    src={createImagePath(selectedMap.name, smokePlaces.side, selectedSmokePlace)}
                                    alt={selectedSmokePlace}
                                    loading="lazy"
                                    fill={true}
                                    className="object-cover"
                                />
                            </div>
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}

export default SelectSmokePlace
