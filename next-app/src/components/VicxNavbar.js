"use client"

import {usePathname} from "next/navigation"
import {Navbar} from "flowbite-react"
import {SITE_PAGES} from "@/constants/sitePages"
import {urlFromBasePath} from "@/util/basePathUtils"

const customTheme = {
    link: {
        base: "block py-2 pl-3 pr-4 md:p-0",
        active: {
            on: "text-base font-bold",
            off: "text-base"
        },
    },
}

export default function VicxNavbar() {
    const pathname = usePathname()

    return <Navbar fluid rounded theme={customTheme}>
        <Navbar.Brand href="/">
            <span className="self-center whitespace-nowrap text-xl font-semibold dark:text-white">VICX</span>
        </Navbar.Brand>
        <Navbar.Toggle/>
        <Navbar.Collapse>
            <Navbar.Link
                href={urlFromBasePath("/")}
                active={pathname === "/"}>Home</Navbar.Link>

            {SITE_PAGES.map(({title, href}, index) =>
                <Navbar.Link
                    key={index}
                    href={urlFromBasePath(href)}
                    active={href === pathname}>{title}</Navbar.Link>)
            }
        </Navbar.Collapse>
    </Navbar>
}