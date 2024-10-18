import {urlFromBasePath} from "@/app/basePathUtils"
import {MAPS} from "@/app/cs/mapConstants"

export const dynamic = "force-static"

export default function CounterStrikePage() {
    return (
        <main className="content">
            <div className="hero" style={{
                background: `linear-gradient(rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.3)), url(${urlFromBasePath("/images/cs2back.jpg")}) center 67%/cover no-repeat`,
            }}>
                <h1>Counter Strike Smokes</h1>
                <p className="lead">Pick a map to get started!</p>
            </div>
            <div>
                <h2 className="text-center text-3xl my-4">Available Maps</h2>
                <div className="container mx-auto grid grid-cols-1 sm:grid-cols-4 gap-4 cursor-pointer ">
                {MAPS.map((map, index) => (
                        <div
                            key={index}
                            className="p-2 flex flex-col items-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg rounded-lg"
                        >
                            <a href={urlFromBasePath(`/cs/${map.name}`)} className="text-center">
                                <img
                                    src={urlFromBasePath(`/images/${map.image}`)}
                                    className="w-32 my-3"
                                    alt={map.name}/>
                                {map.name}
                            </a>
                        </div>
                    ))}
                </div>
            </div>
        </main>
    )
}
