import Hero from "@/components/Hero"
import CalculatorFormAndResult from "@/app/tomcat/CalculatorFormAndResult"

export const metadata = {
    title: "Tomcat | VICX"
}

export default function TomcatPage() {
    return (
        <main className="content">
            <Hero
                title="Simple Calculator"
                lead="Simple Spring Boot calculator hosted by Tomcat"
            />
            <CalculatorFormAndResult/>
        </main>
    )
}
