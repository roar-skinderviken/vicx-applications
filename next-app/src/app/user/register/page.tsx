import Hero from "@/components/Hero"
import UserSignupForm from "@/app/user/register/UserSignupForm"

export const metadata = {
    title: "Sign Up | VICX"
}

const reCaptchaSiteKey = process.env.RECAPTCHA_SITE_KEY || ""

export default function KMeansPage() {
    return (
        <main className="content">
            <Hero
                title="Sign Up"
                lead="Create a new account"
            />

            <UserSignupForm reCaptchaSiteKey={reCaptchaSiteKey}/>
        </main>
    )
}
