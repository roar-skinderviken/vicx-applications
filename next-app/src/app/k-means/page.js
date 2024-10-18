import KMeansFormAndResult from "@/app/k-means/KMeansFormAndResult"

export const dynamic = "force-static"

export default function KMeansPage() {
    return (
        <main className="content">
            <div className="hero">
                <h1>K-Means Clustering</h1>
                <p className="lead">Cluster grades into meaningful groups with the K-Means algorithm</p>
            </div>
            <KMeansFormAndResult/>
        </main>
    )
}
