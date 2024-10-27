import {
    faCubes,
    faGamepad, faHeadset,
    faIdCard, faList,
    faPlay,
    faServer,
    faShieldHalved,
    faTerminal
} from "@fortawesome/free-solid-svg-icons"

import portfolioImage from "../assets/images/portis.png"
import tomcatImage from "../assets/images/tom.png"
import snakeImage from "../assets/images/snake.png"
import kubernetesImage from "../assets/images/kube.png"
import archImage from "../assets/images/arch.png"
import tuxImage from "../assets/images/Tux.png"
import counterStrikeImage from "../assets/images/counter.png"
import esportImage from "../assets/images/esl.png"
import kmeansImage from "../assets/images/kmeans.png"

export const SITE_PAGES = [
    {
        href: "/portfolio",
        image: portfolioImage,
        imgAlt: "Portfolio Image",
        icon: faIdCard,
        title: "Portfolio"
    },
    {
        href: "/tomcat",
        image: tomcatImage,
        imgAlt: "Tomcat Image",
        icon: faServer,
        title: "Tomcat"
    },
    {
        href: "/snake",
        image: snakeImage,
        imgAlt: "Snake Image",
        icon: faGamepad,
        title: "Snake"
    },
    {
        href: "/microk8s",
        image: kubernetesImage,
        imgAlt: "Setup Image",
        icon: faCubes,
        title: "Microk8s",
    },
    {
        href: "/arch",
        image: archImage,
        imgAlt: "Arch Image",
        icon: faTerminal,
        title: "Arch"
    },
    {
        href: "/penetration-testing",
        image: tuxImage,
        imgAlt: "Penetration Testing Image",
        icon: faShieldHalved,
        title: "Pen Testing"
    },
    {
        href: "/cs",
        image: counterStrikeImage,
        imgAlt: "Cs2 Image",
        icon: faPlay,
        title: "Counter Strike"
    },
    {
        href: "/esport",
        image: esportImage,
        imgAlt: "Esport Image",
        icon: faHeadset,
        title: "Esport"
    },
    {
        href: "/k-means",
        image: kmeansImage,
        imgAlt: "K-means Image",
        icon: faList,
        title: "K-means"
    },
]
