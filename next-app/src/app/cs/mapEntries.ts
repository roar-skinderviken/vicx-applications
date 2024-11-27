import {StaticImageData} from "next/image"

import mirageImage from "@/assets/images/mirage.png"
import dust2Image from "@/assets/images/dust2.png"
import anubisImage from "@/assets/images/anubis.png"
import vertigoImage from "@/assets/images/vertigo.png"
import ancientImage from "@/assets/images/ancient.png"
import nukeImage from "@/assets/images/nuke.png"
import infernoImage from "@/assets/images/inferno.png"

import vertigoTGuardianImage from "@/assets/images/smoke-places/vertigo-t-guardian.jpg"
import vertigoTElevatorImage from "@/assets/images/smoke-places/vertigo-t-elevator.jpg"
import vertigoTElevatorAImage from "@/assets/images/smoke-places/vertigo-t-elevator-a.jpg"
import vertigoTCTSpawnImage from "@/assets/images/smoke-places/vertigo-t-ct-spawn.jpg"
import vertigoCTBDefaultImage from "@/assets/images/smoke-places/vertigo-ct-b-default.jpg"
import nukeTTrophyImage from "@/assets/images/smoke-places/nuke-t-trophy.jpg"
import nukeTRampImage from "@/assets/images/smoke-places/nuke-t-ramp.jpg"
import nukeTAVentImage from "@/assets/images/smoke-places/nuke-t-a-vent.jpg"
import nukeCTUnderSiloImage from "@/assets/images/smoke-places/nuke-ct-under-silo.jpg"
import nukeCTTrophyImage from "@/assets/images/smoke-places/nuke-ct-trophy.jpg"
import nukeCTTRedImage from "@/assets/images/smoke-places/nuke-ct-t-red.jpg"
import mirageTStairsImage from "@/assets/images/smoke-places/mirage-t-stairs.jpg"
import mirageTTicketImage from "@/assets/images/smoke-places/mirage-t-ticket.jpg"
import mirageCTPalaceImage from "@/assets/images/smoke-places/mirage-ct-palace.jpg"
import mirageCTTopMidImage from "@/assets/images/smoke-places/mirage-ct-top-mid.jpg"
import mirageTJungleImage from "@/assets/images/smoke-places/mirage-t-jungle.jpg"
import infernoTCoffinsImage from "@/assets/images/smoke-places/inferno-t-coffins.jpg"
import mirageCTApartmentsImage from "@/assets/images/smoke-places/mirage-ct-apartments.jpg"
import infernoTBananaImage from "@/assets/images/smoke-places/inferno-t-banana.jpg"
import anubisTWindowImage from "@/assets/images/smoke-places/anubis-t-window.jpg"
import dust2TBDoorImage from "@/assets/images/smoke-places/dust2-t-b-door.jpg"
import dust2TBWindowImage from "@/assets/images/smoke-places/dust2-t-b-window.jpg"
import dust2TEntranceImage from "@/assets/images/smoke-places/dust2-t-entrance.jpg"
import anubisCTTopMidImage from "@/assets/images/smoke-places/anubis-ct-top-mid.jpg"
import anubisCTConnectorImage from "@/assets/images/smoke-places/anubis-ct-connector.jpg"
import ancientTASiteImage from "@/assets/images/smoke-places/ancient-t-a-site.jpg"
import ancientTDonutImage from "@/assets/images/smoke-places/ancient-t-donut.jpg"
import ancientTMidImage from "@/assets/images/smoke-places/ancient-t-mid.jpg"
import ancientCTTempleImage from "@/assets/images/smoke-places/ancient-ct-temple.jpg"
import ancientCTPyramidImage from "@/assets/images/smoke-places/ancient-ct-pyramid.jpg"
import ancientCTCTAImage from "@/assets/images/smoke-places/ancient-ct-ct-a.jpg"

export type MapSide = "C" | "T"

export interface SideAndSmokePlaces {
    side: MapSide,
    places: SmokePlace[]
}

export interface SmokePlace {
    name: string,
    image: StaticImageData
}

export interface CsMapEntry {
    name: string,
    image: StaticImageData,
    cSide: SmokePlace[],
    tSide: SmokePlace[]
}

export const MAPS: CsMapEntry[] = [
    {
        name: "Mirage",
        image: mirageImage,
        cSide: [
            {name: "Palace", image: mirageCTPalaceImage},
            {name: "Apartments", image: mirageCTApartmentsImage},
            {name: "Top Mid", image: mirageCTTopMidImage}
        ],
        tSide: [
            {name: "Ticket", image: mirageTTicketImage},
            {name: "Stairs", image: mirageTStairsImage},
            {name: "Jungle", image: mirageTJungleImage}
        ]
    },
    {
        name: "Dust2",
        image: dust2Image,
        cSide: [],
        tSide: [
            {name: "Entrance", image: dust2TEntranceImage},
            {name: "B Window", image: dust2TBWindowImage},
            {name: "B Door", image: dust2TBDoorImage}
        ]
    },
    {
        name: "Anubis",
        image: anubisImage,
        cSide: [
            {name: "Top Mid", image: anubisCTTopMidImage},
            {name: "Connector", image: anubisCTConnectorImage}
        ],
        tSide: [
            {name: "Window", image: anubisTWindowImage}
        ]
    },
    {
        name: "Vertigo",
        image: vertigoImage,
        cSide: [
            {name: "B Default", image: vertigoCTBDefaultImage}
        ],
        tSide: [
            {name: "CT Spawn", image: vertigoTCTSpawnImage},
            {name: "Guardian", image: vertigoTGuardianImage},
            {name: "Elevator", image: vertigoTElevatorImage},
            {name: "Elevator A", image: vertigoTElevatorAImage}
        ]
    },
    {
        name: "Ancient",
        image: ancientImage,
        cSide: [
            {name: "CT A", image: ancientCTCTAImage},
            {name: "Temple", image: ancientCTTempleImage},
            {name: "Pyramid", image: ancientCTPyramidImage}
        ],
        tSide: [
            {name: "Mid", image: ancientTMidImage},
            {name: "A Site", image: ancientTASiteImage},
            {name: "Donut", image: ancientTDonutImage}
        ]
    },
    {
        name: "Nuke",
        image: nukeImage,
        cSide: [
            {name: "Under Silo", image: nukeCTUnderSiloImage},
            {name: "T Red", image: nukeCTTRedImage},
            {name: "Trophy", image: nukeCTTrophyImage}
        ],
        tSide: [
            {name: "Ramp", image: nukeTRampImage},
            {name: "Trophy", image: nukeTTrophyImage},
            {name: "A Vent", image: nukeTAVentImage}
        ]
    },
    {
        name: "Inferno",
        image: infernoImage,
        cSide: [],
        tSide: [
            {name: "Banana", image: infernoTBananaImage},
            {name: "Coffins", image: infernoTCoffinsImage}
        ]
    }
]
