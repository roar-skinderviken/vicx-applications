"use client"

import {usePathname} from "next/navigation"
import {Navbar} from "flowbite-react"
import {SITE_PAGES} from "@/constants/sitePages"
import SignedInMenu from "@/components/SignedInMenu"
import {signIn, useSession} from "next-auth/react"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import {faSignInAlt} from "@fortawesome/free-solid-svg-icons"

// see https://flowbite-react.com/docs/components/navbar
const customTheme = {
    root: {
        base: "bg-gray-800 sticky top-0 z-50 px-2 py-3 dark:border-gray-700 dark:bg-gray-800 sm:px-4"
    },
    link: {
        active: {
            off: "border-b border-gray-100 text-gray-400 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white",
        },
    },
}

const signInButton = <button
    onClick={() => signIn(undefined, {callbackUrl: '/dashboard', redirect: true})}
    className="text-gray-400 hover:bg-gray-50 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white rounded-md md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white flex items-center justify-center"
>
    {/* mobile devices */}
    <span className="block md:hidden"><FontAwesomeIcon icon={faSignInAlt} className="mt-1.5 text-gray-400 text-[22px]"/></span>
    {/* big-screen devices */}
    <span className="hidden md:block whitespace-no-wrap">Sign in</span>
</button>

const VicxNavbar = () => {
    const pathname = usePathname()
    const {data: session, status} = useSession()

    return (
        <Navbar fluid theme={customTheme}>
            <Navbar.Brand href="/">
                <span className="self-center whitespace-nowrap text-2xl font-semibold text-white">VICX</span>
            </Navbar.Brand>
            <div className="flex md:order-2 min-w-12 h-8">
                <div className="flex items-center justify-end w-full">
                    <div className="flex-shrink-0 me-4 md:me-0">
                        {status === "unauthenticated" && signInButton}
                        {status === "authenticated" && <SignedInMenu session={session}/>}
                    </div>
                    <Navbar.Toggle/>
                </div>
            </div>
            <Navbar.Collapse>
                <Navbar.Link href="/" active={pathname === "/"}>Home</Navbar.Link>
                {SITE_PAGES.map(({title, href}, index) => (
                    <Navbar.Link
                        key={index}
                        href={href}
                        active={pathname.startsWith(href)}
                    >{title}</Navbar.Link>
                ))}
            </Navbar.Collapse>
        </Navbar>
    )
}

export default VicxNavbar
