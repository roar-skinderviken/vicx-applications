import Hero from "@/components/Hero"
import {getServerSession} from "next-auth"
import {authOptions} from "@/auth"
import CalculatorFormAndResult from "@/app/tomcat/CalculatorFormAndResult"
import {NEXT_APP_PROVIDER} from "@/authProviders"

export const dynamic = "force-dynamic"

export const metadata = {
    title: "Dashboard | VICX"
}

export default async function DashboardPage() {
    const session = await getServerSession(authOptions)

    // @ts-expect-error Because accessToken is not a prop of session
    const provider = session?.provider || ""

    return (
        <main className="content">
            <Hero
                title="Dashboard"
                lead="This is your future dashboard"
            />

            <div className="container mx-auto my-5">
                <h2 className="text-center text-3xl my-4">Greetings {session && session.user?.name}</h2>

                {provider === NEXT_APP_PROVIDER &&
                    <CalculatorFormAndResult useSecureEndpoint={true}/>
                }
            </div>
        </main>
    )
}
