import {Avatar, Dropdown} from "flowbite-react"
import {Session} from "next-auth"
import Link from "next/link"
import {signOut} from "next-auth/react"

import fallbackProfileImage from "@/assets/images/profile.png"

const SignedInMenu = ({session}: { session: Session }) => {
    const {image, name, email} = session.user ?? {}

    return <Dropdown
        arrowIcon={false}
        inline
        label={
            <Avatar
                alt="User settings"
                img={image || fallbackProfileImage.src}
                rounded/>}>
        <Dropdown.Header>
            <span className="block text-sm">{name}</span>
            {email && <span className="block truncate text-sm font-medium">{email}</span>}
        </Dropdown.Header>
        <Dropdown.Item><Link href={"/dashboard"}>Dashboard</Link></Dropdown.Item>
        <Dropdown.Divider/>
        <Dropdown.Item onClick={() => signOut({callbackUrl: '/', redirect: true})}>Sign out</Dropdown.Item>
    </Dropdown>
}

export default SignedInMenu