export {default} from "next-auth/middleware"

export const config = {
    matcher: [
        "/user/calculator",
        "/user/dashboard",
        "/user/image"
    ]
}