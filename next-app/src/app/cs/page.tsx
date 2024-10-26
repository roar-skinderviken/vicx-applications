import Hero from "@/components/Hero"
import Image from "next/image"
import {MAPS} from "@/constants/mapEntries"

export const metadata = {
    title: "Counter Strike Smokes | VICX"
}

export default function CounterStrikePage() {
    return (
        <main className="content">
            <Hero
                title="Counter Strike Smokes"
                lead="Pick a map to get started!"
                backgroundImage="/images/cs2back.jpg"
            />

            <section id="maps">
                <h2 className="text-center text-3xl my-4">Available Maps</h2>
                <div className="container mx-auto grid grid-cols-1 sm:grid-cols-4 gap-4 cursor-pointer ">
                    {MAPS.map(({name, image}, index) => (
                        <div
                            key={index}
                            className="p-2 flex flex-col items-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg rounded-lg"
                        >
                            <a href={`/cs/${name}`} className="text-center">
                                <Image
                                    src={`/images/${image}`}
                                    className="w-32 my-3"
                                    alt={name}
                                    width="512" height="512"/>
                                {name}
                            </a>
                        </div>
                    ))}
                </div>
            </section>
        </main>
    )
}
