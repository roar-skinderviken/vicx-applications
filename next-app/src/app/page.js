import {FontAwesomeIcon} from '@fortawesome/react-fontawesome'
import {
    faCubes,
    faGamepad, faHeadset,
    faIdCard, faList, faPlay,
    faSdCard,
    faServer, faShieldHalved,
    faTerminal,
    faThumbsUp,
} from "@fortawesome/free-solid-svg-icons"
import ItemLink from "@/components/ItemLink"

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
                    <ItemLink
                        href="/portfolio"
                        imgSrc="/images/portis.png"
                        imgAlt="Portfolio Image"
                        imgWidth={67}
                        icon={faIdCard}
                        text="Portfolio"
                    />
                    <ItemLink
                        href="/tomcat"
                        imgSrc="/images/tom.png"
                        imgAlt="Tomcat Image"
                        imgWidth={80}
                        icon={faServer}
                        text="Tomcat"
                    />
                    <ItemLink
                        href="/snake"
                        imgSrc="/images/snake.png"
                        imgAlt="Snake Image"
                        imgWidth={50}
                        icon={faGamepad}
                        text="Snake"
                    />
                    {/*<a href="setup.html" className="list-item">*/}
                    {/*    <img*/}
                    {/*        src="/images/net.png"*/}
                    {/*        alt="Setup Image"*/}
                    {/*        width={55}*/}
                    {/*    />*/}
                    {/*    <FontAwesomeIcon icon={faSdCard} className="fa-fw mr-2"/>*/}
                    {/*    Old Setup*/}
                    {/*</a>*/}
                    {/*<a href="microk8s.html" className="list-item">*/}
                    {/*    <img*/}
                    {/*        src="/images/kube.png"*/}
                    {/*        alt="Setup Image"*/}
                    {/*        width={100}*/}
                    {/*    />*/}
                    {/*    <FontAwesomeIcon icon={faCubes} className="fa-fw mr-2"/>*/}
                    {/*    Microk8s*/}
                    {/*</a>*/}
                    <ItemLink
                        href="/arch"
                        imgSrc="/images/arch.png"
                        imgAlt="Arch Image"
                        imgWidth={65}
                        icon={faTerminal}
                        text="Arch"
                    />
                    <ItemLink
                        href="/penetration-testing"
                        imgSrc="/images/Tux.png"
                        imgAlt="Penetration Testing  Image"
                        imgWidth={55}
                        icon={faShieldHalved}
                        text="Pen Testing"
                    />
                    <ItemLink
                        href="/cs"
                        imgSrc="/images/counter.png"
                        imgAlt="Cs2 Image"
                        imgWidth={75}
                        icon={faPlay}
                        text="Counter Strike"
                    />
                    <ItemLink
                        href="/esport"
                        imgSrc="/images/esl.png"
                        imgAlt="Esport Image"
                        imgWidth={65}
                        icon={faHeadset}
                        text="Esport"
                    />
                    <ItemLink
                        href="/k-means"
                        imgSrc="/images/kmeans.png"
                        imgAlt="K-means Image"
                        imgWidth={65}
                        icon={faList}
                        text="K-means"
                    />
                </div>
            </div>
        </main>
    )
}
