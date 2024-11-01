import {Avatar, Dropdown} from "flowbite-react"
import {Session} from "next-auth"
import Link from "next/link"
import {signIn, signOut} from "next-auth/react"

import fallbackProfileImage from "@/assets/images/profile.png"

import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import {faSignInAlt} from "@fortawesome/free-solid-svg-icons"

const signInButton = <button
    onClick={() => signIn(undefined, {callbackUrl: '/dashboard', redirect: true})}
    className="text-gray-400 hover:bg-gray-50 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white rounded-md md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white flex items-center justify-center"
>
    {/* mobile devices */}
    <span className="block md:hidden"><FontAwesomeIcon icon={faSignInAlt} className="mt-1.5 text-gray-400 text-[22px]"/></span>
    {/* big-screen devices */}
    <span className="hidden md:block whitespace-no-wrap">Sign in</span>
</button>

const renderSignedInMenu = (session: Session) =>
    <Dropdown
        arrowIcon={false}
        inline
        label={
            <Avatar
                alt="User settings"
                img={session.user?.image || fallbackProfileImage.src}
                rounded/>}>
        <Dropdown.Header>
            <span className="block text-sm">{session.user?.name}</span>
        </Dropdown.Header>
        <Dropdown.Item><Link href={"/dashboard"}>Dashboard</Link></Dropdown.Item>
        <Dropdown.Divider/>
        <Dropdown.Item onClick={() => signOut({callbackUrl: '/', redirect: true})}>Sign out</Dropdown.Item>
    </Dropdown>


const AvatarArea = ({session, status}: {
    session: Session | null
    status: "authenticated" | "unauthenticated" | "loading"
}) => status === "authenticated" && session
    ? renderSignedInMenu(session)
    : status === "unauthenticated"
        ? signInButton
        : <></>

export default AvatarArea