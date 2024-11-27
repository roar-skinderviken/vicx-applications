"use client"

import {useRef, useState} from "react"
import Image from "next/image"
import Link from "next/link"
import {Tabs, TabsRef} from "flowbite-react"
import {CsMapEntry, MapSide, SideAndSmokePlaces, SmokePlace} from "@/app/cs/mapEntries"

const CS_PAGE_URL = "/cs#maps"

const SelectSmokePlace = ({selectedMap}: { selectedMap: CsMapEntry }) => {
    const {cSide, tSide} = selectedMap
    const [sideAndSmokePlaces, setSideAndSmokePlaces] = useState<SideAndSmokePlaces>()
    const tabsRef = useRef<TabsRef>(null)

    const handleSideButtonClick = (side: MapSide, places: SmokePlace[]) => {
        setSideAndSmokePlaces({side, places})
        tabsRef.current?.setActiveTab(0)
    }

    return (
        <div className="flex flex-col items-center px-4">
            {/* Button row */}
            <div className="flex flex-nowrap space-x-4 mt-2">
                <button
                    className="cs-side-button bg-blue-500 hover:bg-blue-600"
                    disabled={cSide.length < 1}
                    onClick={() => handleSideButtonClick("C", cSide)}>
                    C-Side
                </button>

                <button
                    className="cs-side-button bg-red-500 hover:bg-red-600"
                    disabled={tSide.length < 1}
                    onClick={() => handleSideButtonClick("T", tSide)}>
                    T-Side
                </button>

                <Link href={CS_PAGE_URL}
                      className="bg-gray-200 text-gray-700 py-2 px-4 rounded shadow hover:bg-gray-300 hover:shadow-md whitespace-nowrap">
                    Back to Map Selector
                </Link>
            </div>

            {sideAndSmokePlaces && (
                <div className="flex flex-col items-center w-full">
                    <h2 className="text-2xl my-6">
                        {sideAndSmokePlaces.side}-Side Smoke Places
                    </h2>

                    <Tabs
                        ref={tabsRef}
                        aria-label="Smoke Places"
                        variant="fullWidth"
                        className="w-full">
                        {sideAndSmokePlaces.places.map(({name, image}, index) => (
                            <Tabs.Item
                                key={index}
                                className="flex justify-center items-center"
                                title={name}>
                                <Image
                                    src={image}
                                    alt={name}
                                    className="object-contain w-full"
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
