import {urlFromBasePath} from "@/app/basePathUtils"

const fetchBackgroundImageUrl = async () => {
    const response = await fetch(
        "https://picsum.photos/1280/720",
        {next: {revalidate: 3600}}
    )

    if (!response.ok) {
        throw new Error('Failed to fetch the image');
    }
    console.log("Fetched the image", response.url)
    return response.url
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
        : <h1 className="animate-fadeInDown">{title}</h1>

    return (
        <div
            className="hero"
            style={{
                background: `url('${imageUrl}') center 67%/cover no-repeat`,
                transition: "background 0.5s ease-in-out", // Smooth transition when image is loaded
            }}>
            {headerElement}
            <p className={`lead${isHomePage ? " text-yellow-300" : ""}  animate-fadeInUp`}>{lead}</p>
        </div>)
}

export default Hero
