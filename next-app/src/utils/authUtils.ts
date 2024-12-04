import {SessionUser} from "@/types/authTypes"

export const hasOneOfRoles = (
    roles: string[],
    sessionUser?: SessionUser): sessionUser is SessionUser  => {
    if (!(sessionUser && sessionUser.roles)) return false
    return sessionUser.roles.some(role => roles.includes(role))
}