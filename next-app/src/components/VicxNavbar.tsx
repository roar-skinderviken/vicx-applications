"use client"

import {usePathname} from "next/navigation"
import {Navbar} from "flowbite-react"
import {SITE_PAGES} from "@/constants/sitePages"

// see https://flowbite-react.com/docs/components/navbar
const customTheme = {
    root: {
        base: "bg-gray-800 px-2 py-2.5 dark:border-gray-700 dark:bg-gray-800 sm:px-4"
    },
    link: {
        active: {
            off: "border-b border-gray-100 text-gray-400 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white",
        },
    },
}

const VicxNavbar = () => {
    const pathname = usePathname()

    return <Navbar fluid theme={customTheme}>
        <Navbar.Brand href="/">
            <span className="self-center whitespace-nowrap text-2xl font-semibold text-white">VICX</span>
        </Navbar.Brand>
        <Navbar.Toggle/>
        <Navbar.Collapse>
            <Navbar.Link
                href="/"
                active={pathname === "/"}>Home</Navbar.Link>

            {SITE_PAGES.map(({title, href}, index) =>
                <Navbar.Link
                    key={index}
                    href={href}
                    active={pathname.startsWith(href)}>{title}</Navbar.Link>)
            }
        </Navbar.Collapse>
    </Navbar>
}

export default VicxNavbar