"use client"

import {redirect, usePathname} from "next/navigation"
import {Navbar} from "flowbite-react"
import {SITE_PAGES} from "@/constants/sitePages"
import SignedInMenu from "@/components/navbar/SignedInMenu"
import {getSession, signIn, signOut} from "next-auth/react"
import {faSignInAlt, faUserPlus} from "@fortawesome/free-solid-svg-icons"
import {navbarTheme, signInOptions, signOutOptions} from "@/components/navbar/navbarConstants"
import {useEffect, useState} from "react"
import {CustomSession, SessionUser} from "@/types/authTypes"
import SignInOrRegisterButton from "@/components/navbar/SignInOrRegisterButton"
import {REFRESH_ACCESS_TOKEN_ERROR} from "@/auth/tokenUtils"
import {Session} from "next-auth"

const EMPTY_USER: SessionUser = {id: ""}

export const getUserOrSignOut = async (session: Session | null) => {
    if (!session) return EMPTY_USER
    const customSession = session as CustomSession

    if (customSession.error === REFRESH_ACCESS_TOKEN_ERROR) {
        await signOut(signOutOptions)
        return EMPTY_USER
    }

    return customSession.user || EMPTY_USER
}

const VicxNavbar = () => {
    const pathname = usePathname()
    const [user, setUser] = useState<SessionUser | null>(null)

    useEffect(() => {
        getSession()
            .then(getUserOrSignOut)
            .then(user => setUser(user))
    }, [])

    return (
        <Navbar fluid theme={navbarTheme}>
            <Navbar.Brand href="/">
                <span className="self-center whitespace-nowrap text-2xl font-semibold text-white">VICX</span>
            </Navbar.Brand>
            <div className="flex md:order-2 min-w-32 h-8">
                <div className="flex items-center justify-end w-full">
                    <div className="flex-shrink-0 me-4 md:me-0">
                        {user && (user.name
                                ? <SignedInMenu user={user}/>
                                : <div className="flex space-x-4">
                                    <SignInOrRegisterButton
                                        icon={faUserPlus}
                                        buttonText="Register"
                                        onClick={() => redirect("/user/register")}
                                    />
                                    <SignInOrRegisterButton
                                        icon={faSignInAlt}
                                        buttonText="Log in"
                                        onClick={async () => await signIn(undefined, signInOptions)}
                                    />
                                </div>
                        )}
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
