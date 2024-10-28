import Hero from "@/components/Hero"
import Image from "next/image"
import {MAPS} from "@/constants/mapEntries"

import csBackgroundImage from "../../assets/images/cs2back.jpg"
import Link from "next/link";

export const metadata = {
    title: "Counter Strike Smokes | VICX"
}

export default function CounterStrikePage() {
    return (
        <main className="content">
            <Hero
                title="Counter Strike Smokes"
                lead="Pick a map to get started!"
                backgroundImage={csBackgroundImage}
            />

            <section id="maps">
                <h2 className="text-center text-3xl my-4">Available Maps</h2>
                <div className="container mx-auto grid grid-cols-1 sm:grid-cols-4 gap-4 cursor-pointer ">
                    {MAPS.map(({name, image}, index) => (
                        <div
                            key={index}
                            className="p-2 flex flex-col items-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg rounded-lg">
                            <Link href={`/cs/${name}`} className="text-center">
                                <Image
                                    src={image}
                                    className="my-3 w-32"
                                    alt={name}/>
                                {name}
                            </Link>
                        </div>
                    ))}
                </div>
            </section>
        </main>
    )
}
