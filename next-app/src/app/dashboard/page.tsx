import Hero from "@/components/Hero"
import {getSession} from "@/auth";

export const metadata = {
    title: "Tomcat | VICX"
}

export default async function DashboardPage() {
    const session = await getSession()

    return (
        <main className="content">
            <Hero
                title="Dashboard"
                lead="This is your future dashboard"
            />

            <div className="container mx-auto my-5">
                <h2 className="text-center text-3xl my-4">Greetings {session && session.user?.name}</h2>
            </div>
        </main>
)
}
