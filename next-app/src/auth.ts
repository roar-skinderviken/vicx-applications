import {getServerSession, NextAuthOptions} from "next-auth"
import "next-auth/jwt"
import {Provider} from "next-auth/providers/index"
import GitHubProvider from "next-auth/providers/github"

const springBootProvider: Provider = {
    id: "next-app-client",
    clientId: "next-app-client",
    clientSecret: process.env.OIDC_CLIENT_SECRET || "secret",
    name: "Spring Boot OAuth",
    version: "2.0",
    type: "oauth",
    checks: ["state"],
    idToken: true,
    authorization: {
        url: process.env.AUTHORIZATION_URL || "http://localhost:9000/oauth2/authorize",
        params: {scope: "openid"}
    },
    token: process.env.TOKEN_URL || "http://localhost:9000/oauth2/token",
    issuer: process.env.ISSUER || "http://localhost:9000",
    jwks_endpoint: process.env.JWKS_ENDPOINT || "http://localhost:9000/oauth2/jwks",

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    profile: (profile: any) => {
        console.log("profile", profile)
        return {
            id: profile.sub,
            name: profile.name || profile.username,
            email: profile.email,
        }
    }
}

const providers = [springBootProvider]
if (process.env.GITHUB_ID) {
    providers.push(GitHubProvider({
        clientId: process.env.GITHUB_ID || "",
        clientSecret: process.env.GITHUB_SECRET || ""
    }))
}

const authOptions = {
    providers: providers,
    session: {
        strategy: "jwt"
    },
    callbacks: {
        async jwt({token, account, profile}) {
            // console.log("jwt token", JSON.stringify(token || {}))
            // console.log("account", JSON.stringify(account || {}))
            // console.log("profile", JSON.stringify(profile || {}))
            // Persist the OAuth access_token and or the user id to the token right after signin
            if (account) {
                token.accessToken = account.access_token
            }
            if (profile) {
                token.id = profile.sub
                token.name = profile.name
                token.picture = profile.image
            }
            return token
        },
        async session({session, token}) {
            if (session.user) {
                session.user.name = token.name || token.sub
                session.user.image = token.picture
            }
            //console.log("session session", JSON.stringify(session || {}))
            //console.log("Inside session")
            // console.log("session token", JSON.stringify(token || {}))
            return session
        }
    }
} satisfies NextAuthOptions

/**
 * Helper function to get the session on the server without having to import the authOptions object every single time
 * @returns The session object or null
 */
const getSession = async () => await getServerSession(authOptions)

export {authOptions, getSession}