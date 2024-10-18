import ItemLink from "@/components/ItemLink"
import {SITE_PAGES} from "@/constants/sitePages"

export const dynamic = "force-static"

export default function Home() {
    return (
        <main className="content">
            <div className="hero">
                <div className="container mx-auto">
                    <h1 className="text-center flex justify-center">
                        <img
                            src="/images/logo-no-background.png"
                            alt="Welcome to VICX!"
                            width={400}
                            height={400}
                            className="w-[400px]"
                        />
                    </h1>
                    <p className="lead text-yellow-300 text-center">Explore my showcase of recent work</p>
                </div>
            </div>

            <div className="container mx-auto">
                <h2 className="text-center text-3xl my-4">Table of Contents</h2>

                <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
                    {SITE_PAGES.map((page, index) =>
                        <ItemLink
                            key={index}
                            href={page.href}
                            imgSrc={page.imgSrc}
                            imgAlt={page.imgAlt}
                            imgWidth={page.imgWidth}
                            icon={page.icon}
                            text={page.title}
                        />
                    )}
                </div>
            </div>
        </main>
    )
}
