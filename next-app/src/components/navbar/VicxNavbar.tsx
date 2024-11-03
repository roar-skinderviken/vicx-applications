"use client"

import {usePathname} from "next/navigation"
import {Navbar} from "flowbite-react"
import {SITE_PAGES} from "@/constants/sitePages"
import SignedInMenu from "@/components/navbar/SignedInMenu"
import {signIn} from "next-auth/react"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import {faSignInAlt} from "@fortawesome/free-solid-svg-icons"
import {SessionUser} from "@/types/authTypes"
import {navbarTheme, signInOptions} from "@/components/navbar/navbarConstants"
import {useSessionUser} from "@/components/navbar/useSessionUser"

const signInButton = (
    <button
        onClick={
            async () => await signIn(undefined, signInOptions)
        }
        className="text-gray-400 hover:bg-gray-50 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white rounded-md md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white flex items-center justify-center"
    >
        {/* mobile devices */}
        <span className="block md:hidden">
            <FontAwesomeIcon icon={faSignInAlt} className="mt-1.5 text-gray-400 text-[22px]"/>
        </span>
        {/* big-screen devices */}
        <span className="hidden md:block whitespace-no-wrap">Sign in</span>
    </button>)

const VicxNavbar = ({serverSideUser}: { serverSideUser?: SessionUser }) => {
    const pathname = usePathname()
    const user = useSessionUser(serverSideUser)

    return (
        <Navbar fluid theme={navbarTheme}>
            <Navbar.Brand href="/">
                <span className="self-center whitespace-nowrap text-2xl font-semibold text-white">VICX</span>
            </Navbar.Brand>
            <div className="flex md:order-2 min-w-12 h-8">
                <div className="flex items-center justify-end w-full">
                    <div className="flex-shrink-0 me-4 md:me-0">
                        {user?.name
                            ? <SignedInMenu user={user}/>
                            : signInButton
                        }
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
