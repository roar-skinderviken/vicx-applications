import {render, screen} from "@testing-library/react"
import Hero from "@/components/Hero"

import expectedBackgroundImage from "@/assets/images/cs2back.jpg"

const expectedStyle = (image: string) => `background: url(${image}) center 67%/cover no-repeat`

describe("Hero", () => {
    describe("Layout", () => {

        it("displays title when only title and lead is provided", () => {
            render(<Hero title="Hero Title" lead="Hero Lead"/>)
            expect(screen.queryByText("Hero Title")).toBeInTheDocument()
        })

        it("does not display title when when isHomePage is provided as true", () => {
            render(<Hero title="Hero Title" lead="Hero Lead" isHomePage={true}/>)
            expect(screen.queryByText("Hero Title")).not.toBeInTheDocument()
        })

        it("displays lead when isHomePage is not provided", () => {
            render(<Hero title="Hero Title" lead="Hero Lead"/>)

            const lead = screen.queryByText("Hero Lead")
            expect(lead).toBeInTheDocument()
            expect(lead).not.toHaveClass("text-yellow-300")
        })

        it("displays lead in yellow when isHomePage is provided as true", () => {
            render(<Hero title="Hero Title" lead="Hero Lead" isHomePage={true}/>)

            const lead = screen.queryByText("Hero Lead")
            expect(lead).toBeInTheDocument()
            expect(lead).toHaveClass("text-yellow-300")
        })

        it("displays logo image as title when isHomePage is provided as true", () => {
            render(<Hero title="Hero Title" lead="Hero Lead" isHomePage={true}/>)
            expect(screen.queryByAltText("Welcome to VICX!")).toBeInTheDocument()
        })

        it("displays hero-bg-image as bg image when backgroundImage is not provided", () => {
            const {container} = render(<Hero title="Hero Title" lead="Hero Lead"/>)
            const outerDiv = container.querySelector("div.text-white.py-16.text-center")

            expect(outerDiv).toHaveStyle(expectedStyle("/api/hero-bg-image"))
        })

        it("displays provided bg image when provided", () => {
            const {container} =
                render(<Hero
                    title="Hero Title"
                    lead="Hero Lead"
                    backgroundImage={expectedBackgroundImage}/>)

            const outerDiv = container.querySelector("div.text-white.py-16.text-center")
            expect(outerDiv).toHaveStyle(expectedStyle(expectedBackgroundImage.src))
        })
    })
})