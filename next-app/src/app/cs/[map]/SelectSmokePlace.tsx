"use client"

import {useRef, useState} from "react"
import Image from "next/image"
import Link from "next/link"
import {CsMapEntry} from "@/constants/mapEntries"
import {Tabs, TabsRef} from "flowbite-react";

const INITIAL_SMOKE_PLACES = {side: "", places: [""]}
const CS_PAGE_URL = "/cs#maps"

const createImagePath = (mapName: string, side: string, smokePlace: string) => {
    const cleanString = (str: string) => str.replace(/\s+/g, '-').toLowerCase()
    return `/images/smoke-places/${cleanString(mapName)}-${side}-${cleanString(smokePlace)}.jpg`
}

const SelectSmokePlace = ({selectedMap}: { selectedMap: CsMapEntry }) => {
    const {ct, t} = selectedMap
    const [smokePlaces, setSmokePlaces] = useState(INITIAL_SMOKE_PLACES)
    const tabsRef = useRef<TabsRef>(null)

    const handleClick = (side: string, places: string[]) => {
        setSmokePlaces({side, places})
        tabsRef.current?.setActiveTab(0)
    }

    return (
        <div className="flex flex-col items-center px-4">
            {/* Button row */}
            <div className="flex flex-nowrap space-x-4 mt-2">
                <button
                    className="cs-side-button bg-blue-500 hover:bg-blue-600"
                    disabled={ct.length < 1}
                    onClick={() => handleClick("ct", ct)}>
                    C-Side
                </button>

                <button
                    className="cs-side-button bg-red-500 hover:bg-red-600"
                    disabled={t.length < 1}
                    onClick={() => handleClick("t", t)}>
                    T-Side
                </button>

                <Link href={CS_PAGE_URL}
                      className="bg-gray-200 text-gray-700 py-2 px-4 rounded shadow hover:bg-gray-300 hover:shadow-md whitespace-nowrap">
                    Back to Map Selector
                </Link>
            </div>

            {smokePlaces.side && (
                <div className="flex flex-col items-center w-full">
                    <h2 className="text-2xl my-6">
                        {smokePlaces.side === "ct" ? "C-Side" : "T-Side"} Smoke Places
                    </h2>

                    <Tabs
                        ref={tabsRef}
                        aria-label="Smoke Places"
                        variant="fullWidth"
                        className="w-full">
                        {smokePlaces.places.map((smokePlace, index) => (
                            <Tabs.Item
                                key={index}
                                title={smokePlace}>
                                <Image
                                    src={createImagePath(selectedMap.name, smokePlaces.side, smokePlace)}
                                    alt={smokePlace}
                                    loading="lazy"
                                    width={1920}
                                    height={1080}
                                    className="object-contain"
                                />
                            </Tabs.Item>
                        ))}
                    </Tabs>
                </div>
            )}
        </div>
    )
}

export default SelectSmokePlace
