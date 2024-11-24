import {NextAuthOptions, Session} from "next-auth"
import "next-auth/jwt"
import {JWT} from "next-auth/jwt"
import {githubProvider, DEFAULT_APP_PROVIDER_ID, springBootProvider} from "@/constants/authProviders"

const OAUTH_BASE_URL = process.env.OAUTH_BASE_URL || "http://localhost:9000/auth-server"

async function refreshAccessToken(token: JWT) {
    try {
        const response = await fetch(`${OAUTH_BASE_URL}/oauth2/token`, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            method: "POST",
            body: new URLSearchParams({
                client_id: "next-app-client",
                client_secret: process.env.OAUTH_CLIENT_SECRET || "secret",
                grant_type: "refresh_token",
                refresh_token: token.refreshToken,
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
            error: "RefreshAccessTokenError",
        }
    }
}

const providers = [springBootProvider]
if (process.env.GITHUB_ID) {
    providers.unshift(githubProvider)
}

const authOptions = {
    providers: providers,
    session: {
        strategy: "jwt",
        maxAge: 60 * 55, // 55 minutes
    },
    callbacks: {
        async jwt({token, user, account}) {
            // Initial sign in
            if (account && user) {
                if (account.provider !== DEFAULT_APP_PROVIDER_ID) {
                    return {
                        ...token,
                        provider: account.provider,
                        user
                    }
                }

                return {
                    provider: account.provider,
                    accessToken: account.accessToken,
                    accessTokenExpires: Date.now() + Number(account.expires_in) * 1000,
                    refreshToken: account.refresh_token,
                    user,
                }
            }

            const expiresInSeconds = Math.floor((Number(token.accessTokenExpires) - Date.now()) / 1000)
            console.debug("Access token expires in:", expiresInSeconds)

            // Return previous token if the access token has not expired yet
            if (token.provider !== DEFAULT_APP_PROVIDER_ID || expiresInSeconds > 10) {
                return token
            }

            // Access token has expired, try to update it
            return refreshAccessToken(token)
        },
        async session({session, token}) {
            if (token) {
                return {
                    ...session,
                    user: token.user,
                    provider: token.provider,
                    accessToken: token.accessToken,
                    error: token.error
                } as Session
            }
            return session
        },
    }
} satisfies NextAuthOptions

export default authOptions