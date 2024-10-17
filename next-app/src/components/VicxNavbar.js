"use client"

import {usePathname} from "next/navigation"
import {Navbar} from "flowbite-react"

const customTheme = {
    link: {
        base: "block py-2 pl-3 pr-4 md:p-0",
        active: {
            on: "text-base font-bold",
            off: "text-base"
        },
    },
}

const LINKS = [
    {name: 'Home', href: '/'},
    {name: 'Portfolio', href: '/portfolio'},
    {name: 'Tomcat', href: '/tomcat'},
    {name: 'Snake', href: '/snake'},
    {name: 'Esport', href: '/esport'},
    {name: 'K-means', href: '/k-means'},
]

export default function VicxNavbar() {
    const pathname = usePathname()

    return <Navbar fluid rounded theme={customTheme}>
            <Navbar.Brand href="/">
                <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">VICX</span>
            </Navbar.Brand>
            <Navbar.Toggle/>
            <Navbar.Collapse>
                {
                    LINKS.map(({name, href}) =>
                        <Navbar.Link
                            key={href}
                            href={href}
                            active={href === pathname}
                        >{name}</Navbar.Link>)
                }
            </Navbar.Collapse>
        </Navbar>
}