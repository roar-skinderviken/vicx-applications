import {NextAuthOptions, Session} from "next-auth"
import "next-auth/jwt"
import {
    githubProvider,
    DEFAULT_OAUTH_CLIENT_ID,
    springBootProvider
} from "@/auth/authProviders"
import {refreshAccessToken, revokeToken} from "@/auth/tokenUtils"

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
                if (account.provider !== DEFAULT_OAUTH_CLIENT_ID) {
                    return {
                        ...token,
                        provider: account.provider,
                        accessToken: account.access_token,
                        user
                    }
                }

                return {
                    provider: account.provider,
                    accessToken: account.access_token,
                    accessTokenExpires: Date.now() + Number(account.expires_in) * 1000,
                    refreshToken: account.refresh_token,
                    user,
                }
            }

            const expiresInSeconds = Math.floor((Number(token.accessTokenExpires) - Date.now()) / 1000)
            console.debug("Access token expires in:", expiresInSeconds)

            // Return previous token if the access token has not expired yet
            if (token.error || token.provider !== DEFAULT_OAUTH_CLIENT_ID || expiresInSeconds > 10) {
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
    },
    events: {
        async signOut({token}) {
            await revokeToken(token)
        }
    }
} satisfies NextAuthOptions

export default authOptions