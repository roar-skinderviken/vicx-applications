import {urlFromBasePath} from "@/app/basePathUtils"
import {MAPS} from "@/app/cs/mapConstants"
import Hero from "@/components/Hero"

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

            <div>
                <h2 className="text-center text-3xl my-4">Available Maps</h2>
                <div className="container mx-auto grid grid-cols-1 sm:grid-cols-4 gap-4 cursor-pointer ">
                    {MAPS.map(({name, image}, index) => (
                        <div
                            key={index}
                            className="p-2 flex flex-col items-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg rounded-lg"
                        >
                            <a href={urlFromBasePath(`/cs/${name}`)} className="text-center">
                                <img
                                    src={urlFromBasePath(`/images/${image}`)}
                                    className="w-32 my-3"
                                    alt={name}/>
                                {name}
                            </a>
                        </div>
                    ))}
                </div>
            </div>
        </main>
    )
}
