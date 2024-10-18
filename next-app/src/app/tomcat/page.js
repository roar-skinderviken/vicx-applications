import CalculatorFormAndResult from "@/app/tomcat/CalculatorFormAndResult"

export const dynamic = "force-static"

export default function KMeansPage() {
    return (
        <main className="content">
            <div className="hero">
                <h1>Simple Calculator</h1>
                <p className="lead">Simple Spring Boot calculator hosted by Tomcat</p>
            </div>
            <CalculatorFormAndResult/>
        </main>
    )
}
