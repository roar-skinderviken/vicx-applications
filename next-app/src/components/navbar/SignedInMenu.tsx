import {Avatar, Dropdown} from "flowbite-react"
import {signOut} from "next-auth/react"

import fallbackProfileImage from "@/assets/images/profile.png"
import {SessionUser} from "@/types/authTypes"
import {signOutOptions} from "@/components/navbar/navbarConstants"
import Link from "next/link"

const SignedInMenu = ({user}: { user: SessionUser }) => {
    const {image, name, email} = user

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
        <Link href={"/user/dashboard"}><Dropdown.Item>Dashboard</Dropdown.Item></Link>
        <Dropdown.Divider/>
        <Dropdown.Item onClick={
            async () => await signOut(signOutOptions)
        }>Sign out</Dropdown.Item>
    </Dropdown>
}

export default SignedInMenu