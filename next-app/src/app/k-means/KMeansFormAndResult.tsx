"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {InferType} from "yup"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"

// put this in next-app/.env.local
// NEXT_PUBLIC_KMEANS_BACKEND_URL=http://localhost:8000/k-means
const BACKEND_URL = process.env.NEXT_PUBLIC_KMEANS_BACKEND_URL || "/backend-python/k-means"

const kMeansSchema = yup.object({
    maxScore: yup
        .number()
        .typeError("Max Score must be a number")
        .required("Max Score is required")
        .min(10, "Max Score must be greater than 10")
        .max(1000, "Max Score must be less than 1000"),
    failScore: yup
        .number()
        .typeError("Fail Score must be a number")
        .required("Fail Score is required")
        .min(0, "Fail Score must be equal or greater than 0"),
    scores: yup
        .string()
        .required("Scores are required")
        .test("is-valid-list", "There should be at least 5 scores", (value) =>
            value.split(",").length >= 5)
        .test("all-numbers", "All scores must be numbers", (value) =>
            value.split(",").every(entry => !isNaN(Number(entry)) && entry !== "")),
    maxIter: yup
        .number()
        .typeError("Max Iterations must be a number")
        .required("Max Iterations is required")
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

type KMeansSuccessResponse = Record<number, Grade>

interface KMeansErrorResponse {
    error: string
}

type KMeansResponse = KMeansSuccessResponse | KMeansErrorResponse

const isErrorResponse = (response: KMeansResponse): response is KMeansErrorResponse =>
    'error' in response

const KMeansFormAndResult = () => {
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [result, setResult] = useState<KMeansResponse>()

    const methods = useForm<KMeansFormData>({
        resolver: yupResolver(kMeansSchema),
        mode: "onChange",
        defaultValues: {
            maxIter: 300
        }
    })

    const {handleSubmit, formState} = methods

    const onSubmit = async (formData: KMeansFormData) => {
        setIsLoading(true)
        setResult(undefined)

        const requestBody = {
            ...formData,
            scores: formData.scores.split(',').map(Number)
        }

        const fetchConfig = {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(requestBody)
        }

        fetch(BACKEND_URL, fetchConfig)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok')
                }
                return response.json()
            })
            .then(data => setResult(data))
            .catch(error => console.error("Error:", error))
            .finally(() => setIsLoading(false))
    }

    return (
        <div className="container mx-auto my-10 text-center">
            <div className="flex justify-center">
                <FormProvider {...methods}>
                    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 max-w-md">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            <ValidatedTextInput label="Max Score" name="maxScore"/>
                            <ValidatedTextInput label="Fail Score" name="failScore"/>
                            <ValidatedTextInput label="Scores (comma separated)" name="scores"/>
                            <ValidatedTextInput label="Max Iterations" name="maxIter"/>
                            <ButtonWithSpinner
                                disabled={!formState.isValid}
                                buttonText="Submit"
                                isLoading={isLoading}
                                className="col-span-1 sm:col-span-2 mt-4 w-full"
                            />
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