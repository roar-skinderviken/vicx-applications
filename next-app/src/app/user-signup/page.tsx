import Hero from "@/components/Hero"
import UserSignupForm from "@/app/user-signup/UserSignupForm"

export const metadata = {
    title: "Sign Up | VICX"
}

export default function KMeansPage() {
    return (
        <main className="content">
            <Hero
                title="Sign Up"
                lead="Create a new account"
            />

            <UserSignupForm/>
        </main>
    )
}
