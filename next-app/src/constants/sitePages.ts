import {
    faCubes,
    faGamepad, faHeadset,
    faIdCard, faList,
    faPlay,
    faServer,
    faShieldHalved,
    faTerminal
} from "@fortawesome/free-solid-svg-icons"

export const SITE_PAGES = [
    {
        href: "/portfolio",
        imgSrc: "/images/portis.png",
        imgAlt: "Portfolio Image",
        imgWidth: 512,
        imgHeight: 512,
        icon: faIdCard,
        title: "Portfolio"
    },
    {
        href: "/tomcat",
        imgSrc: "/images/tom.png",
        imgAlt: "Tomcat Image",
        imgWidth: 1200,
        imgHeight: 855,
        icon: faServer,
        title: "Tomcat"
    },
    {
        href: "/snake",
        imgSrc: "/images/snake.png",
        imgAlt: "Snake Image",
        imgWidth: 488,
        imgHeight: 640,
        icon: faGamepad,
        title: "Snake"
    },
    {
        href: "/microk8s",
        imgSrc: "/images/kube.png",
        imgAlt: "Setup Image",
        imgWidth: 600,
        imgHeight: 300,
        icon: faCubes,
        title: "Microk8s",
    },
    {
        href: "/arch",
        imgSrc: "/images/arch.png",
        imgAlt: "Arch Image",
        imgWidth: 1200,
        imgHeight: 1200,
        icon: faTerminal,
        title: "Arch"
    },
    {
        href: "/penetration-testing",
        imgSrc: "/images/Tux.png",
        imgAlt: "Penetration Testing Image",
        imgWidth: 265,
        imgHeight: 314,
        icon: faShieldHalved,
        title: "Pen Testing"
    },
    {
        href: "/cs",
        imgSrc: "/images/counter.png",
        imgAlt: "Cs2 Image",
        imgWidth: 200,
        imgHeight: 200,
        icon: faPlay,
        title: "Counter Strike"
    },
    {
        href: "/esport",
        imgSrc: "/images/esl.png",
        imgAlt: "Esport Image",
        imgWidth: 2360,
        imgHeight: 2140,
        icon: faHeadset,
        title: "Esport"
    },
    {
        href: "/k-means",
        imgSrc: "/images/kmeans.png",
        imgAlt: "K-means Image",
        imgWidth: 200,
        imgHeight: 200,
        icon: faList,
        title: "K-means"
    },
]
