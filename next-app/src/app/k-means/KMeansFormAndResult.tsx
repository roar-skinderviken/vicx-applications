"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedInput from "@/components/ValidatedInput"
import {Button} from "flowbite-react"
import {InferType} from "yup"

// put this in next-app/.env.local
// NEXT_PUBLIC_KMEANS_BACKEND_URL=http://localhost:8000/k-means
const BACKEND_URL = process.env.NEXT_PUBLIC_KMEANS_BACKEND_URL || "/backend-python/k-means"

const kMeansSchema = yup.object({
    max_score: yup
        .number()
        .typeError("Max Score must be a number")
        .min(10, "Max Score must be greater than 10")
        .max(1000, "Max Score must be less than 1000"),
    fail_grade: yup
        .number()
        .typeError("Fail Score must be a number")
        .min(0, "Fail Score must be equal or greater than 0"),
    grades: yup
        .string()
        .required("Scores are required")
        .test("is-valid-list", "There should be at least 5 scores", (value) => {
            return value.split(",").length >= 5
        })
        .test("all-numbers", "All scores must be numbers", (value) => {
            return value.split(",").every(entry => !isNaN(Number(entry)) && entry !== "")
        }),
    max_iter: yup
        .number()
        .typeError("Max Iterations must be a number")
        .min(1, "Max Iterations must be greater than 0")
        .max(999, "Max Iterations must be less than 1000"),
})

type Grade = "A" | "B" | "C" | "D" | "E" | "F"

const gradeToColorMap: Record<Grade, string> = {
    A: "bg-green-500",
    B: "bg-blue-500",
    C: "bg-orange-500",
    D: "bg-yellow-400",
    E: "bg-purple-500",
    F: "bg-red-500"
}

type KMeansFormData = InferType<typeof kMeansSchema>

interface KMeansSuccessResponse {
    [key: number]: Grade
}

interface KMeansErrorResponse {
    error: string
}

type KMeansResponse = KMeansSuccessResponse | KMeansErrorResponse

const isErrorResponse = (response: KMeansResponse): response is KMeansErrorResponse =>
    'error' in response

const KMeansFormAndResult = () => {
    const [result, setResult] = useState<KMeansResponse | null>(null)

    const methods = useForm<KMeansFormData>({
        resolver: yupResolver(kMeansSchema),
        mode: "onChange"
    })

    const {handleSubmit, formState} = methods

    const onSubmit = async (formData: KMeansFormData) => {
        const requestBody = {
            maxScore: formData.max_score,
            failScore: formData.fail_grade,
            scores: formData.grades.split(',').map(Number),
            maxIter: formData.max_iter
        }

        try {
            const response = await fetch(BACKEND_URL, {
                method: "POST",
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(requestBody)
            })

            if (!response.ok) {
                console.error("Error: Network response was not ok")
                return
            }

            const data: KMeansResponse = await response.json()
            setResult(data)
        } catch (error) {
            console.error("Error:", error)
        }
    }

    return (
        <div className="container mx-auto my-10 text-center">
            <div className="flex justify-center">
                <FormProvider {...methods}>
                    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 max-w-md">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            <ValidatedInput label="Max Score" name="max_score"/>
                            <ValidatedInput label="Fail Score" name="fail_grade"/>
                            <ValidatedInput label="Scores (comma separated)" name="grades"/>
                            <ValidatedInput label="Max Iterations" name="max_iter" defaultValue="300"/>
                            <Button
                                type="submit"
                                disabled={!formState.isValid}
                                className="col-span-1 sm:col-span-2 mt-4 w-full"
                            >Submit</Button>
                        </div>
                    </form>
                </FormProvider>
            </div>
            {result && (
                <div className="m-4">
                    {isErrorResponse(result) ? (
                        <div
                            className="text-red-600 font-bold text-xl bg-red-100 border border-red-400 rounded p-4 mx-4">
                            {result.error}
                        </div>
                    ) : (
                        Object.entries((result as KMeansSuccessResponse)).map(([score, grade], index) =>
                            <div
                                key={index}
                                className={`mt-2 p-2 rounded text-white ${gradeToColorMap[(grade as Grade)]}`}
                            >Score: {score}, Grade: {grade}</div>
                        )
                    )}
                </div>
            )}
        </div>
    )
}

export default KMeansFormAndResult