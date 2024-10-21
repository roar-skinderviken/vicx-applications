"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedInput from "@/components/ValidatedInput"
import {Button} from "flowbite-react"

// put this in next-app/.env.local
// NEXT_PUBLIC_KMEANS_BACKEND_URL=http://localhost:8000/k-means
const BACKEND_URL = process.env.NEXT_PUBLIC_KMEANS_BACKEND_URL || "/backend-python/k-means"

const schema = yup.object().shape({
    max_score: yup
        .number()
        .typeError("Max Score must be a number")
        .required("Max Score is required")
        .min(10, "Max Score must be greater than 10")
        .max(1000, "Max Score must be less than 1000"),
    fail_grade: yup
        .number()
        .typeError("Fail Score must be a number")
        .required("Fail Score is required")
        .min(0, "Fail Score must be greater than 0"),
    grades: yup
        .string()
        .required("Grades are required")
        .test("is-valid-list", "There should be at least 5 scores", (value) => {
            return value && value.split(",").length >= 5
        }),
    max_iter: yup
        .number()
        .typeError("Max Iterations must be a number")
        .required("Max Iterations are required")
        .min(1, "Max Iterations must be greater than 0")
        .max(1000, "Max Iterations must be less than 1000"),
})

const gradeToColorMap = {
    A: "bg-green-500",
    B: "bg-blue-500",
    C: "bg-orange-500",
    D: "bg-yellow-400",
    E: "bg-purple-500",
    F: "bg-red-500"
}

const KMeansFormAndResult = () => {
    const [result, setResult] = useState()

    const methods = useForm({
        resolver: yupResolver(schema),
        mode: "onChange"
    })

    const {handleSubmit, formState} = methods

    const onSubmit = (formData) => {
        const requestBody = {
            maxScore: parseFloat(formData.max_score),
            failScore: parseFloat(formData.fail_grade),
            scores: formData.grades.split(',').map(Number),
            maxIter: parseInt(formData.max_iter)
        }

        fetch(BACKEND_URL, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(requestBody)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok')
                }
                return response.json()
            })
            .then(data => setResult(data))
            .catch(error => console.error("Error:", error))
    }

    // noinspection JSUnresolvedReference
    return (
        <div className="container mx-auto my-10 text-center">
            <div className="flex justify-center">
                <FormProvider {...methods}>
                    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 max-w-md">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            <ValidatedInput label="Max Score" name="max_score"/>
                            <ValidatedInput label="Fail Score" name="fail_grade"/>
                            <ValidatedInput label="Scores (comma separated)" name="grades"/>
                            <ValidatedInput label="Max Iterations" name="max_iter" defaultValue={300}/>
                            <Button
                                type="submit"
                                disabled={!formState.isValid}
                                className="col-span-1 sm:col-span-2 mt-4 w-full"
                            >Submit</Button>
                        </div>
                    </form>
                </FormProvider>
            </div>
            {result && <div className="m-4">
                {result.error
                    ? <div className="text-red-600 font-bold text-xl bg-red-100 border border-red-400 rounded p-4 mx-4">
                        {result.error}
                    </div>
                    : Object.entries(result).map(([score, grade], index) =>
                        <div
                            key={index}
                            className={`mt-2 p-2 rounded text-white ${gradeToColorMap[grade]}`}
                        >Score: {score}, Grade: {grade}</div>
                    )
                }
            </div>}
        </div>
    )
}

export default KMeansFormAndResult