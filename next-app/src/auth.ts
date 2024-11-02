import {NextAuthOptions} from "next-auth"
import "next-auth/jwt"
import {Provider} from "next-auth/providers/index"
import GitHubProvider from "next-auth/providers/github"

const springBootProvider: Provider = {
    id: "next-app-client",
    name: "Vicx OAuth",
    clientId: "next-app-client",
    clientSecret: process.env.OIDC_CLIENT_SECRET || "secret",
    version: "2.0",
    type: "oauth",
    checks: ["pkce", "state"],
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
            name: profile.sub
        }
    }
}

const providers = [springBootProvider]
if (process.env.GITHUB_ID) {
    providers.unshift(
        GitHubProvider({
            clientId: process.env.GITHUB_ID || "",
            clientSecret: process.env.GITHUB_SECRET || "",
            // profile(profile: GithubProfile) {
            //     return {
            //         id: profile.id.toString(),
            //         name: profile.name,
            //         userName: profile.login,
            //         email: profile.email,
            //         image: profile.avatar_url,
            //     }
            // },
        })
    )
}

const debug = false

const authOptions = {
    providers: providers,
    session: {
        strategy: "jwt"
    },
    callbacks: {
        async jwt({token, account, profile, trigger}) {
            if (debug) {
                console.log("Start jwt")
                console.log("jwt token", JSON.stringify(token || {}))
                console.log("account", JSON.stringify(account || {}))
                console.log("profile", JSON.stringify(profile || {}))
            }

            if (account) {
                token.accessToken = account.access_token
            }

            if (trigger === "signIn" && profile) {
                if (debug) console.log("jwt: Inside if-test")

                token.id = profile.sub
                token.name = profile.name || profile.email || profile.sub
                token.picture = profile.image
                token.email = profile.email
            }
            if (debug) console.log("End jwt")
            return token
        },

        async session({session, token}) {
            if (debug) console.log("Start session")
            if (session.user) {
                if (debug) {
                    console.log("session: Inside if-test")
                    console.log("token", JSON.stringify(token))
                }
                session.user.name = token.name
                session.user.email = token.email
                session.user.image = token.picture
            }
            if (debug) console.log("End session")
            return session
        }
    }
} satisfies NextAuthOptions

export {authOptions}