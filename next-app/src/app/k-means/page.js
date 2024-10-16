import KMeansFormAndResult from "@/components/KMeansFormAndResult"

export const dynamic = "force-static"

export default function KMeansPage() {
    return (
        <main className="content">
            <div className="hero">
                <div className="container">
                    <h1><span>K-Means Clustering</span></h1>
                    <p className="lead">Cluster grades into meaningful groups with the K-Means algorithm</p>
                </div>
            </div>
            <KMeansFormAndResult/>
        </main>
    )
}
