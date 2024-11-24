import Hero from "@/components/Hero"
import UserSignupForm from "@/app/user/register/UserSignupForm"

export const dynamic = "force-dynamic"

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

            <UserSignupForm reCaptchaSiteKey={process.env.RECAPTCHA_SITE_KEY as string}/>
        </main>
    )
}
