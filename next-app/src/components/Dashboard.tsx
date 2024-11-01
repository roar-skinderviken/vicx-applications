"use client"

import {useSession} from "next-auth/react"

const Dashboard = () => {
    const {data: session} = useSession()

    return <h2 className="text-center text-3xl my-4">Greetings {session && session.user?.name}</h2>
}

export default Dashboard