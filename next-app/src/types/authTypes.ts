import {Session, User} from "next-auth"

export interface SessionUser extends User {
    roles?: string[] | null
}

export interface CustomSession extends  Omit<Session, "user"> {
    user?: SessionUser | null
    provider?: string | null
    accessToken?: string | null
    error?: string | null
}

