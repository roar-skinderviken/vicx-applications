import CounterStrikeScreen from "@/app/cs/CounterStrikeScreen"

export const dynamic = "force-static"

export default function CounterStrikePage() {
    return (
        <main className="content">
            <div className="hero cs-hero">
                <h1><span>Counter Strike Smokes</span></h1>
                <p className="lead">Pick a map to get started!</p>
            </div>
            <CounterStrikeScreen/>
        </main>
    )
}
