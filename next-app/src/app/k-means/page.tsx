import KMeansFormAndResult from "@/app/k-means/KMeansFormAndResult"
import Hero from "@/components/Hero"

export const metadata = {
    title: "K-Means | VICX"
}

export default function KMeansPage() {
    return (
        <main className="content">
            <Hero
                title="K-Means Clustering"
                lead="Cluster grades into meaningful groups with the K-Means algorithm"
            />

            <KMeansFormAndResult/>
        </main>
    )
}
