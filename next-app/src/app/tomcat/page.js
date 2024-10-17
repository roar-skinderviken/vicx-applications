import CalculatorFormAndResult from "@/app/tomcat/CalculatorFormAndResult"
import React from "react"

export const dynamic = "force-static"

export default function KMeansPage() {
    return (
        <main className="content">
            <div className="hero">
                <h1><span>Simple Calculator</span></h1>
                <p className="lead">Simple Spring Boot calculator hosted by Tomcat</p>
            </div>
            <CalculatorFormAndResult/>
        </main>
    )
}
