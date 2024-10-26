import {SITE_PAGES} from "@/constants/sitePages"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import Hero from "@/components/Hero"
import Image from "next/image"

export default function Home() {
    return (
        <main className="content">
            <Hero
                title=""
                lead="Explore my showcase of recent work"
                isHomePage={true}
            />

            <div className="container mx-auto">
                <h2 className="text-center text-3xl my-4">Table of Contents</h2>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    {SITE_PAGES.map((page, index) => (
                        <a href={page.href}
                           key={index}
                           className="flex items-center justify-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg mb-4 p-2"
                        >
                            <Image
                                src={page.imgSrc}
                                alt={page.imgAlt}
                                className="w-20 max-h-[80px] h-auto object-contain"
                                width={page.imgWidth}
                                height={page.imgHeight}
                            />
                            <FontAwesomeIcon icon={page.icon} className="fa-fw mx-2"/>
                            {page.title}
                        </a>
                    ))}
                </div>
            </div>
        </main>
    )
}
