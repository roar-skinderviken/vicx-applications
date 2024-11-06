import {NextAuthOptions, Session} from "next-auth"
import "next-auth/jwt"
import {JWT} from "next-auth/jwt";
import {githubProvider, springBootProvider} from "@/authProviders";

async function refreshAccessToken(token: JWT) {
    try {
        const url = process.env.TOKEN_URL || "http://localhost:9000/auth-server/oauth2/token"

        const response = await fetch(url, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            method: "POST",
            body: new URLSearchParams({
                client_id: "next-app-client",
                client_secret: process.env.OIDC_CLIENT_SECRET || "secret",
                grant_type: "refresh_token",
                refresh_token: token.refreshToken,
            } as Record<string, string>).toString()
        })

        const refreshedTokens = await response.json()

        if (!response.ok) {
            // noinspection ExceptionCaughtLocallyJS
            throw refreshedTokens
        }

        console.log("Response access token", refreshedTokens)

        return {
            ...token,
            accessToken: refreshedTokens.access_token,
            accessTokenExpires: Date.now() + refreshedTokens.expires_in * 1000,
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
        strategy: "jwt"
    },
    callbacks: {
        async jwt({token, user, account}) {
            // Initial sign in
            if (account && user) {
                return {
                    accessToken: account.accessToken,
                    accessTokenExpires: Date.now() + Number(account.expires_in) * 1000,
                    refreshToken: account.refresh_token,
                    user,
                }
            }

            // Return previous token if the access token has not expired yet
            if (Date.now() < Number(token.accessTokenExpires)) {
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
                    accessToken: token.accessToken,
                    error: token.error
                } as Session
            }
            return session
        },
    }
} satisfies NextAuthOptions

export {authOptions}