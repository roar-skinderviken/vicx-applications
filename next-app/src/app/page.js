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

                <div className="flex flex-col items-center">
                    <div className="w-full max-w-md text-center">
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
                        {/*<a href="arch.html" className="list-item">*/}
                        {/*    <img*/}
                        {/*        src="/images/arch.png"*/}
                        {/*        alt="Arch Image"*/}
                        {/*        width={65}*/}
                        {/*    />*/}
                        {/*    <FontAwesomeIcon icon={faTerminal} className="fa-fw mr-2"/>*/}
                        {/*    Arch*/}
                        {/*</a>*/}
                        {/*<a href="pen.html" className="list-item">*/}
                        {/*    <img*/}
                        {/*        src="/images/Tux.png"*/}
                        {/*        alt="Arch Image"*/}
                        {/*        width={55}*/}
                        {/*    />*/}
                        {/*    <FontAwesomeIcon icon={faShieldHalved} className="fa-fw mr-2"/>*/}
                        {/*    Pen Testing*/}
                        {/*</a>*/}
                        {/*<a href="cs.html" className="list-item">*/}
                        {/*    <img*/}
                        {/*        src="/images/counter.png"*/}
                        {/*        alt="Cs2 Image"*/}
                        {/*        width={75}*/}
                        {/*    />*/}
                        {/*    <FontAwesomeIcon icon={faPlay} className="fa-fw mr-2"/>*/}
                        {/*    Counter Strike*/}
                        {/*</a>*/}
                        {/*<a href="esport.html" className="list-item">*/}
                        {/*    <img*/}
                        {/*        src="/images/esl.png"*/}
                        {/*        alt="Esport Image"*/}
                        {/*        width={65}*/}
                        {/*    />*/}
                        {/*    <FontAwesomeIcon icon={faHeadset} className="fa-fw mr-2"/>*/}
                        {/*    Esport*/}
                        {/*</a>*/}
                        <ItemLink
                            href="k-means"
                            imgSrc="/images/kmeans.png"
                            imgAlt="K-means Image"
                            imgWidth={65}
                            icon={faList}
                            text="K-means"
                        />
                    </div>
                </div>
            </div>
        </main>
    )
}
