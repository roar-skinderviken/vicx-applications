"use client"

import {useState} from "react"
import Link from "next/link"
import {urlFromBasePath} from "@/util/basePathUtils"

const INITIAL_SMOKE_PLACES = {side: "", places: []}

const createImagePath = (mapName, side, smokePlace) => {
    const cleanString = (str) => str.replace(/\s+/g, '-').toLowerCase()
    return urlFromBasePath(`/images/smoke-places/${cleanString(mapName)}-${side}-${cleanString(smokePlace)}.jpg`)
}

const SelectSmokePlace = ({selectedMap}) => {
    const [smokePlaces, setSmokePlaces] = useState(INITIAL_SMOKE_PLACES)
    const [selectedSmokePlace, setSelectedSmokePlace] = useState("")

    // noinspection HtmlUnknownTarget
    return (
        <div className="flex flex-col items-center">
            {/* Button row */}
            <div className="flex space-x-4 mt-4">
                <button
                    className="cs-side-button bg-blue-500 hover:bg-blue-600"
                    disabled={selectedMap.ct.length < 1}
                    onClick={() => {
                        setSelectedSmokePlace(selectedMap.ct[0])
                        setSmokePlaces({side: "ct", places: selectedMap.ct})
                    }}>
                    C-Side
                </button>

                <button
                    className="cs-side-button bg-red-500 hover:bg-red-600"
                    disabled={selectedMap.t.length < 1}
                    onClick={() => {
                        setSelectedSmokePlace(selectedMap.t[0])
                        setSmokePlaces({side: "t", places: selectedMap.t})
                    }}>
                    T-Side
                </button>

                <Link href="/cs"
                      className="bg-gray-200 text-gray-700 py-2 px-4 rounded shadow hover:bg-gray-300 hover:shadow-md"
                >Back to Map Selector</Link>
            </div>

            {smokePlaces && smokePlaces.side &&
                <div className="flex flex-col items-center mt-5">
                    <h2
                        className="text-center text-3xl my-4">
                        {smokePlaces.side === "ct" ? "C-Side" : "T-Side"} Smoke Places
                    </h2>

                    {/* Tabs */}
                    <div
                        className="text-sm font-medium text-center text-gray-500 border-b border-gray-200 dark:text-gray-400 dark:border-gray-700">
                        <ul className="flex flex-wrap -mb-px">
                            {smokePlaces.places.map((smokePlace, index) => (
                                <li className="mr-2" key={index}>
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
                                src={createImagePath(selectedMap.name, smokePlaces.side, selectedSmokePlace)}
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

export default SelectSmokePlace
