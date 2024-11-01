"use client"

import {usePathname} from "next/navigation"
import {useSession, signIn, signOut} from "next-auth/react"
import {Avatar, Dropdown, Navbar} from "flowbite-react"
import {SITE_PAGES} from "@/constants/sitePages"
import Link from "next/link"
import fallbackProfileImage from "@/assets/images/profile.png"

// see https://flowbite-react.com/docs/components/navbar
const customTheme = {
    root: {
        base: "bg-gray-800 sticky top-0 z-50 px-2 py-2.5 dark:border-gray-700 dark:bg-gray-800 sm:px-4"
    },
    link: {
        active: {
            off: "border-b border-gray-100 text-gray-400 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white",
        },
    },
}

const VicxNavbar = () => {
    const pathname = usePathname()
    const {data: session, status} = useSession()

    return <Navbar fluid theme={customTheme}>
        <Navbar.Brand href="/">
            <span className="self-center whitespace-nowrap text-2xl font-semibold text-white">VICX</span>
        </Navbar.Brand>
        {session && <div className="flex md:order-2">
            <Dropdown
                arrowIcon={false}
                inline
                label={<Avatar alt="User settings" img={session.user?.image || fallbackProfileImage.src} rounded />}>
                <Dropdown.Header>
                    <span className="block text-sm">{session.user?.name}</span>
                </Dropdown.Header>
                <Dropdown.Item><Link href={"/dashboard"}>Dashboard</Link></Dropdown.Item>
                <Dropdown.Divider/>
                <Dropdown.Item onClick={() => signOut({ callbackUrl: '/', redirect:true })}>Sign out</Dropdown.Item>
            </Dropdown>
        </div>}
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
            {status === "unauthenticated" && (
                <Navbar.Link
                    onClick={() => signIn(undefined, { callbackUrl: '/dashboard', redirect:true })}
                    className="cursor-pointer"
                >Sign in</Navbar.Link>
            )}
        </Navbar.Collapse>
    </Navbar>
}

export default VicxNavbar
