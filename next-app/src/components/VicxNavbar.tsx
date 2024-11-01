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
        base: "bg-gray-800 sticky top-0 z-50 px-2 py-4 dark:border-gray-700 dark:bg-gray-800 sm:px-4"
    },
    link: {
        active: {
            off: "border-b border-gray-100 text-gray-400 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white",
        },
    },
}

const VicxNavbar = () => {
    const pathname = usePathname();
    const {data: session, status} = useSession();

    let avatarArea = <div style={{width: '72px', height: '40px'}}/>

    if (status === "unauthenticated") {
        avatarArea = (
            <button
                onClick={() => signIn(undefined, {callbackUrl: '/dashboard', redirect: true})}
                style={{width: '72px', height: '40px'}}
                className="cursor-pointer text-gray-400 hover:bg-gray-50 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white px-3 py-2 rounded-md md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white h-10 flex items-center"
            >
                Sign in
            </button>
        );
    }

    if (status === "authenticated") {
        avatarArea = (
            <Dropdown
                arrowIcon={false}
                inline
                label={
                    <Avatar
                        alt="User settings"
                        img={session.user?.image || fallbackProfileImage.src}
                        rounded
                        style={{width: '72px', height: '40px'}} // Set fixed dimensions
                    />
                }
            >
                <Dropdown.Header>
                    <span className="block text-sm">{session.user?.name}</span>
                </Dropdown.Header>
                <Dropdown.Item>
                    <Link href={"/dashboard"}>Dashboard</Link>
                </Dropdown.Item>
                <Dropdown.Divider/>
                <Dropdown.Item onClick={() => signOut({callbackUrl: '/', redirect: true})}>Sign out</Dropdown.Item>
            </Dropdown>
        );
    }

    return (
        <Navbar fluid theme={customTheme}>
            <Navbar.Brand href="/">
                <span className="self-center whitespace-nowrap text-2xl font-semibold text-white">VICX</span>
            </Navbar.Brand>
            <div className="flex md:order-2">
                {avatarArea}
            </div>
            <Navbar.Toggle/>
            <Navbar.Collapse>
                <Navbar.Link href="/" active={pathname === "/"}>Home</Navbar.Link>
                {SITE_PAGES.map(({title, href}, index) => (
                    <Navbar.Link key={index} href={href} active={pathname.startsWith(href)}>{title}</Navbar.Link>
                ))}
            </Navbar.Collapse>
        </Navbar>
    );
}


export default VicxNavbar
