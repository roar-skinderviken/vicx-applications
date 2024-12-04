import Hero from "@/components/Hero"
import {getServerSession} from "next-auth"
import authOptions from "@/auth/authOptions"
import UpdatePasswordForm from "@/app/user/dashboard/components/UpdatePasswordForm"
import {SessionUser} from "@/types/authTypes"
import ProfileCard from "@/app/user/dashboard/components/ProfileCard"

export const dynamic = "force-dynamic"

export const metadata = {
    title: "Dashboard | VICX"
}

export default async function DashboardPage() {
    const session = await getServerSession(authOptions)
    const sessionUser = session?.user
    const isVicxUser = (sessionUser as SessionUser)?.roles?.includes("ROLE_USER")

    const nonVicxUser = isVicxUser
        ? undefined
        : {
            username: (sessionUser as SessionUser)?.id as string,
            name: sessionUser?.name as string,
            email: sessionUser?.email as string,
            image: sessionUser?.image || undefined
        }

    return (
        <main className="content">
            <Hero
                title="Dashboard"
                lead="Here you can change your settings"
            />

            <div className="container mx-auto mt-5 mb-2">
                <h2 className="text-center text-3xl my-4">
                    Greetings {session?.user?.name || "User"}
                </h2>

                <div className="flex flex-col items-center mx-4">
                    <div className="max-w-3xl mx-auto text-center">
                        {!isVicxUser ? (
                                <>
                                    <p className="text-lg">
                                        Since you logged in with GitHub, account details such as name, email, and
                                        password cannot be
                                        modified here.
                                    </p>
                                    <p className="mt-4">
                                        To make changes, please update your information directly in your GitHub account.
                                    </p>
                                </>)
                            : (
                                <p className="text-lg text-gray-600">
                                    Welcome to your dashboard! Here, you can easily manage and update your account
                                    details.
                                </p>
                            )}
                    </div>
                    {sessionUser && <ProfileCard nonVicxUserInfo={nonVicxUser}/>}
                    {isVicxUser && <UpdatePasswordForm cardTitle="Change password"/>}
                </div>
            </div>
        </main>
    )
}
