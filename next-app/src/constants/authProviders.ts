import {Provider} from "next-auth/providers/index"
import GitHubProvider, {GithubProfile} from "next-auth/providers/github"

export const DEFAULT_APP_PROVIDER_ID = "next-app-client"
export const GITHUB_PROVIDER_ID = "github"

export const githubProvider = GitHubProvider({
    id: GITHUB_PROVIDER_ID,
    clientId: process.env.GITHUB_ID || "",
    clientSecret: process.env.GITHUB_SECRET || "",
    profile(profile: GithubProfile) {
        return {
            id: profile.id.toString(),
            name: profile.name || profile.login, // if user has not registered name
            roles: [],
            email: profile.email,
            image: profile.avatar_url,
        }
    }
})

export const springBootProvider: Provider = {
    id: DEFAULT_APP_PROVIDER_ID,
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
        return {
            id: profile.sub,
            name: profile.sub,
            roles: profile.roles,
        }
    }
}
