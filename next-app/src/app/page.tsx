import {SITE_PAGES} from "@/constants/sitePages"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import Hero from "@/components/Hero"
import Image from "next/image"
import Link from "next/link"

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
                        <Link href={page.href}
                              key={index}
                              data-testid={`page-link-${index}`}
                              className="flex items-center justify-center transition-transform duration-200 ease-in-out hover:scale-105 hover:shadow-lg mb-4 p-2">
                            <Image
                                src={page.image}
                                alt={page.imgAlt}
                                className="w-20 max-h-[80px] object-contain"/>
                            <FontAwesomeIcon icon={page.icon} className="fa-fw mx-2"/>
                            {page.title}
                        </Link>
                    ))}
                </div>
            </div>
        </main>
    )
}
