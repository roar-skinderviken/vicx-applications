import {SessionUser} from "@/types/authTypes"

export const hasRole = (
    role: string,
    sessionUser?: SessionUser): sessionUser is SessionUser  =>
    sessionUser?.roles?.includes(role) || false

export const hasOneOfRoles = (
    roles: string[],
    sessionUser?: SessionUser): sessionUser is SessionUser  => {
    if (!(sessionUser && sessionUser.roles)) return false
    return sessionUser.roles.some(role => roles.includes(role))
}