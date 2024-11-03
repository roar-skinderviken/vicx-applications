import {SessionUser} from "@/types/authTypes"
import {useEffect, useState} from "react"
import {areEqual} from "@/components/navbar/navbarUtils"

export const useSessionUser = (initialUser?: SessionUser) => {
    const [user, setUser] = useState<SessionUser>(initialUser ?? {})

    useEffect(() => {
        fetch("/api/auth/session")
            .then(res => {
                if (!res.ok) {
                    throw new Error('Network response was not ok')
                }
                return res.json()
            })
            .then(data => {
                // Only update if the new user is different
                setUser(prevUser =>
                    areEqual(data.user, prevUser) ? prevUser : data.user)
            })
            .catch(error => console.error("Error:", error))
    }, [])

    return user
}
