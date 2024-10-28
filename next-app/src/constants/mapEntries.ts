import {StaticImageData} from "next/image";

export interface CsMapEntry {
    name: string,
    image: StaticImageData,
    ct: string[],
    t: string[],
}

import mirageImage from "@/assets/images/mirage.png"
import dust2Image from "@/assets/images/dust2.png"
import anubisImage from "@/assets/images/anubis.png"
import vertigoImage from "@/assets/images/vertigo.png"
import ancientImage from "@/assets/images/ancient.png"
import nukeImage from "@/assets/images/nuke.png"
import infernoImage from "@/assets/images/inferno.png"

export const MAPS: CsMapEntry[] = [
    {
        name: "Mirage",
        image: mirageImage,
        ct: ["Palace", "Apartments", "Top Mid"],
        t: ["Ticket", "Stairs", "Jungle"]
    },
    {
        name: "Dust2",
        image: dust2Image,
        ct: [],
        t: ["Entrance", "B Window", "B Door"]
    },
    {
        name: "Anubis",
        image: anubisImage,
        ct: ["Top Mid", "Connector"],
        t: ["Window"]
    },
    {
        name: "Vertigo",
        image: vertigoImage,
        ct: ["B Default"],
        t: ["CT Spawn", "Guardian", "Elevator", "Elevator A"]
    },
    {
        name: "Ancient",
        image: ancientImage,
        ct: ["CT A", "Temple", "Pyramid"],
        t: ["Mid", "A Site", "Donut"]
    },
    {
        name: "Nuke",
        image: nukeImage,
        ct: ["Under Silo", "T Red", "Trophy"],
        t: ["Ramp", "Trophy", "A Vent"]
    },
    {
        name: "Inferno",
        image: infernoImage,
        ct: [],
        t: ["Banana", "Coffins"]
    },
]
