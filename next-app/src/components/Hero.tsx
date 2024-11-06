import Image, {StaticImageData} from "next/image"
import logoImage from "@/assets/images/logo-no-background.png"

const DEFAULT_BG_IMAGE_URL = "/api/hero-bg-image"

const Hero = ({
                  title,
                  lead,
                  backgroundImage,
                  isHomePage = false,
              }: {
    title: string
    lead: string
    backgroundImage?: StaticImageData
    isHomePage?: boolean
}) => {
    const headerElement = isHomePage
        ? (
            <h1 className="flex justify-center animate-fadeInDown">
                <Image
                    src={logoImage}
                    alt="Welcome to VICX!"
                    priority={true}
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
            className="text-white py-16 text-center w-full"
            style={{
                background: `url(${backgroundImage?.src || DEFAULT_BG_IMAGE_URL}) center/cover no-repeat`,
                transition: "background 0.5s ease-in-out",
                backgroundSize: "cover",
                backgroundPosition: "center"
            }}>
            {headerElement}
            <p className={`${isHomePage ? "text-yellow-300 " : ""}animate-fadeInUp`}>
                {lead}
            </p>
        </div>
    )
}

export default Hero
