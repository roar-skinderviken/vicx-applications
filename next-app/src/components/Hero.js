import {urlFromBasePath} from "@/app/basePathUtils"

const FALLBACK_IMAGE = "/images/hero-fallback.jpg"
const IMAGE_EXPIRATION_IN_SECS = 900 // 15 minutes

const fetchBackgroundImageUrl = async () => {
    try {
        const response = await fetch(
            "https://picsum.photos/1280/720",
            {next: {revalidate: IMAGE_EXPIRATION_IN_SECS}}
        )

        if (!response.ok) {
            return FALLBACK_IMAGE
        }

        return response.url
    } catch (error) {
        return FALLBACK_IMAGE
    }
}

const Hero = async ({title, lead, backgroundImage = undefined, isHomePage = false}) => {
    const imageUrl = backgroundImage || await fetchBackgroundImageUrl()

    const headerElement = isHomePage
        ? <h1 className="flex justify-center animate-fadeInDown">
            <img
                src={urlFromBasePath("/images/logo-no-background.png")}
                alt="Welcome to VICX!"
                width={400}
                height={400}
                className="w-[400px]"/>
        </h1>
        : <h1 className="animate-fadeInDown text-xl mb-5 sm:text-2xl md:text-4xl">
            {title}
        </h1>

    return (
        <div
            className="text-white py-24 text-center"
            style={{
                background: `url('${imageUrl}') center 67%/cover no-repeat`,
                transition: "background 0.5s ease-in-out",
            }}>
            {headerElement}
            <p className={`${isHomePage ? "text-yellow-300" : ""}  animate-fadeInUp`}>{lead}</p>
        </div>)
}

export default Hero
