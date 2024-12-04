import {Session, User} from "next-auth"

export interface SessionUser extends User {
    roles?: string[]
}

export interface CustomSession extends Session {
    user?: SessionUser
    provider?: string
    accessToken?: string
    error?: string
}
