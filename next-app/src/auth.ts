import {getServerSession, NextAuthOptions} from "next-auth"
import "next-auth/jwt"
import {Provider} from "next-auth/providers/index"

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
    // userinfo: process.env.USERINFO_URL || "http://localhost:9000/userinfo",
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

const authOptions= {
    providers: [springBootProvider],
    session: {
        strategy: "jwt"
    },
    callbacks: {
        session({session, token}) {
            if (session.user) {
                session.user.name = token.sub
            }
            return session
        }
    }
} satisfies NextAuthOptions

/**
 * Helper function to get the session on the server without having to import the authOptions object every single time
 * @returns The session object or null
 */
const getSession = () => getServerSession(authOptions)

export {authOptions, getSession}