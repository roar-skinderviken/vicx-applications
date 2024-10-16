"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import KMeansInput from "@/components/KMeansInput"

// const url = "http://localhost:8000/k-means"  //"/backend-python/k-means"
const url = "/backend-python/k-means"

const schema = yup.object().shape({
    max_score: yup
        .number()
        .typeError("Maximum Score must be a number")
        .required("Maximum Score is required")
        .min(10, "Maximum Score must be greater than 10")
        .max(1000, "Maximum Score must be less than 1000"),
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

        fetch(url, {
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

    return (
        <div className="container mx-auto my-10 text-center">
            <div className="flex justify-center">
                <FormProvider {...methods}>
                    <form id="gradesForm" onSubmit={handleSubmit(onSubmit)}>
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 max-w-md">
                            <KMeansInput label="Maximum Score" name="max_score"/>
                            <KMeansInput label="Fail Score" name="fail_grade"/>
                            <KMeansInput label="Scores (comma separated)" name="grades"/>
                            <KMeansInput label="Max Iterations" name="max_iter" defaultValue={300}/>
                        </div>
                        <button
                            type="submit"
                            className="mt-4 w-full py-2 px-4 rounded ${formState.isValid ? 'bg-cyan-500 hover:bg-cyan-700 text-white' : 'bg-gray-400 cursor-not-allowed'}`"
                            disabled={!formState.isValid}
                        >
                            Submit
                        </button>
                    </form>
                </FormProvider>
            </div>
            <div id="results" className="mt-8">
                {result && Object.entries(result)
                    .sort(([keyA], [keyB]) => keyB - keyA)
                    .map(([score, grade]) => (
                        <div key={score} className={`grade ${grade}`}>
                            Score: {score}, Grade: {grade}
                        </div>
                    ))}
            </div>
        </div>
    )
}

export default KMeansFormAndResult