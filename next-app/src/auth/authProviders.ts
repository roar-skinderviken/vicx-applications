import {Provider} from "next-auth/providers/index"
import GitHubProvider, {GithubProfile} from "next-auth/providers/github"

export const DEFAULT_OAUTH_CLIENT_ID = "next-app-client"
export const GITHUB_PROVIDER_ID = "github"

export const githubProvider = GitHubProvider({
    id: GITHUB_PROVIDER_ID,
    clientId: process.env.GITHUB_ID || "",
    clientSecret: process.env.GITHUB_SECRET || "",
    checks: ["state"],
    profile(profile: GithubProfile) {
        return {
            id: profile.login,
            name: profile.name || profile.login, // if user has not registered name
            roles: ["ROLE_GITHUB_USER"],
            email: profile.email,
            image: profile.avatar_url,
        }
    }
})

const OAUTH_BASE_URL = process.env.OAUTH_BASE_URL || "http://localhost:9000/auth-server"

export const springBootProvider: Provider = {
    id: DEFAULT_OAUTH_CLIENT_ID,
    name: "Vicx OAuth",
    clientId: "next-app-client",
    clientSecret: process.env.OAUTH_CLIENT_SECRET || "secret",
    type: "oauth",
    checks: ["pkce", "state", "nonce"],
    idToken: true,
    wellKnown: `${OAUTH_BASE_URL}/.well-known/openid-configuration`,
    authorization: {
        url: `${OAUTH_BASE_URL}/oauth2/authorize`,
        params: {scope: "openid profile email"}
    },

    // this exists as well
    // "http://localhost:9000/auth-server/.well-known/oauth-authorization-server",

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    profile: (profile: any) => {
        return {
            id: profile.sub,
            name: profile.name || profile.sub, // if user has not registered name
            email: profile.email,
            image: profile.image && `/api/user/image?${profile.sub}`,
            roles: profile.roles
        }
    }
}
