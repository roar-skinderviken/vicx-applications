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
        imgWidth: 67,
        icon: faIdCard,
        title: "Portfolio"
    },
    {
        href: "/tomcat",
        imgSrc: "/images/tom.png",
        imgAlt: "Tomcat Image",
        imgWidth: 80,
        icon: faServer,
        title: "Tomcat"
    },
    {
        href: "/snake",
        imgSrc: "/images/snake.png",
        imgAlt: "Snake Image",
        imgWidth: 50,
        icon: faGamepad,
        title: "Snake"
    },
    {
        href: "/microk8s",
        imgSrc: "/images/kube.png",
        imgAlt: "Setup Image",
        imgWidth: 100,
        icon: faCubes,
        title: "Microk8s",
    },
    {
        href: "/arch",
        imgSrc: "/images/arch.png",
        imgAlt: "Arch Image",
        imgWidth: 65,
        icon: faTerminal,
        title: "Arch"
    },
    {
        href: "/penetration-testing",
        imgSrc: "/images/Tux.png",
        imgAlt: "Penetration Testing Image",
        imgWidth: 55,
        icon: faShieldHalved,
        title: "Pen Testing"
    },
    {
        href: "/cs",
        imgSrc: "/images/counter.png",
        imgAlt: "Cs2 Image",
        imgWidth: 75,
        icon: faPlay,
        title: "Counter Strike"
    },
    {
        href: "/esport",
        imgSrc: "/images/esl.png",
        imgAlt: "Esport Image",
        imgWidth: 65,
        icon: faHeadset,
        title: "Esport"
    },
    {
        href: "/k-means",
        imgSrc: "/images/kmeans.png",
        imgAlt: "K-means Image",
        imgWidth: 65,
        icon: faList,
        title: "K-means"
    },
]
