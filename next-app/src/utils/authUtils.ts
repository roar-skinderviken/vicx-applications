import {SessionUser} from "@/types/authTypes"

export const hasRole = (role: string, sessionUser: SessionUser | null | undefined): boolean =>
    sessionUser?.roles?.includes(role) || false