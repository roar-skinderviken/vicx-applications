import {SessionUser} from "@/types/authTypes"

export const hasRole = (
    role: string,
    sessionUser?: SessionUser): sessionUser is SessionUser  =>
    sessionUser?.roles?.includes(role) || false