export interface CsMapEntry {
    name: string,
    image: string,
    ct: string[],
    t: string[],
}

export const MAPS: CsMapEntry[] = [
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
