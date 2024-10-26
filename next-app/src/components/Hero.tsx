import Image from "next/image"

const Hero = ({title, lead, backgroundImage = undefined, isHomePage = false}: {
    title: string,
    lead: string,
    backgroundImage?: string,
    isHomePage?: boolean
}) => {
    const bgImageUrl = backgroundImage || "/api/hero-bg-image"

    const headerElement = isHomePage
        ? (
            <h1 className="flex justify-center animate-fadeInDown">
                <Image
                    src={"/images/logo-no-background.png"}
                    alt="Welcome to VICX!"
                    width={400}
                    height={400}
                    className="w-[400px]"/>
            </h1>
        )
        : (
            <h1 className="animate-fadeInDown text-xl mb-5 sm:text-2xl md:text-4xl">
                {title}
            </h1>
        )

    return (
        <div
            className="text-white py-24 text-center"
            style={{
                background: `url(${bgImageUrl}) center 67%/cover no-repeat`,
                transition: "background 0.5s ease-in-out",
            }}>
            {headerElement}
            <p className={`${isHomePage ? "text-yellow-300" : ""} animate-fadeInUp`}>{lead}</p>
        </div>
    )
}

export default Hero
