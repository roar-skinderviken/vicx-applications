import {JWT} from "next-auth/jwt"
import {DEFAULT_OAUTH_CLIENT_ID} from "@/auth/authProviders"
import {Session} from "next-auth"
import {CustomSession} from "@/types/authTypes"
import {signOut} from "next-auth/react"
import {signOutOptions} from "@/components/navbar/navbarConstants"

const OAUTH_BASE_URL = process.env.OAUTH_BASE_URL || "http://localhost:9000/auth-server"
export const REFRESH_ACCESS_TOKEN_ERROR = "RefreshAccessTokenError"

export const extractUserOrSignOut = async (session: Session | null) => {
    if (!session) return undefined
    const customSession = session as CustomSession

    if (customSession.error === REFRESH_ACCESS_TOKEN_ERROR) {
        await signOut(signOutOptions)
        return Promise.reject(REFRESH_ACCESS_TOKEN_ERROR)
    } else {
        return customSession.user
    }
}

export async function revokeToken(token: JWT) {
    try {
        const response = await fetch(`${OAUTH_BASE_URL}/oauth2/revoke`, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({
                client_id: DEFAULT_OAUTH_CLIENT_ID,
                client_secret: process.env.OAUTH_CLIENT_SECRET || "secret",
                token: token.refreshToken
            } as Record<string, string>).toString()
        })

        if (response.ok) {
            console.log("Token successfully revoked.")
        } else {
            console.log("Token revocation failed.")
        }
    } catch (error) {
        console.log(error)
    }
}

export async function refreshAccessToken(token: JWT) {
    try {
        const response = await fetch(`${OAUTH_BASE_URL}/oauth2/token`, {
            method: "POST",
            headers: {"Content-Type": "application/x-www-form-urlencoded"},
            body: new URLSearchParams({
                client_id: DEFAULT_OAUTH_CLIENT_ID,
                client_secret: process.env.OAUTH_CLIENT_SECRET || "secret",
                grant_type: "refresh_token",
                refresh_token: token.refreshToken
            } as Record<string, string>).toString()
        })

        const refreshedTokens = await response.json()

        if (!response.ok) {
            // noinspection ExceptionCaughtLocallyJS
            throw refreshedTokens
        }

        return {
            ...token,
            accessToken: refreshedTokens.access_token,
            accessTokenExpires: Date.now() + (Number(refreshedTokens.expires_in) * 1000),
            refreshToken: refreshedTokens.refresh_token ?? token.refreshToken, // Fall back to old refresh token
        }
    } catch (error) {
        console.log(error)

        return {
            ...token,
            error: REFRESH_ACCESS_TOKEN_ERROR
        }
    }
}
