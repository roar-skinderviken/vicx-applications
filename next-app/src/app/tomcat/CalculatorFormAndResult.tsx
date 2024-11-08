"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {Button} from "flowbite-react"
import {InferType} from "yup"
import {getSession} from "next-auth/react"
import {CustomSession} from "@/types/authTypes"
import {hasRole} from "@/utils/authUtils"

// put this in next-app/.env.local
// NEXT_PUBLIC_TOMCAT_BACKEND_URL=http://localhost:8080/api
export const CALC_BACKEND_BASE_URL = process.env.NEXT_PUBLIC_TOMCAT_BACKEND_URL || "/backend-spring-boot/api/calculator"

const calculatorYupSchema = yup.object({
    firstValue: yup
        .number()
        .typeError("First value must be a number"),
    secondValue: yup
        .number()
        .typeError("Second value must be a number"),
    operation: yup
        .string()
        .required("Operation is required"),
})

type CalculatorFormData = InferType<typeof calculatorYupSchema>

interface CalculatorResult extends CalculatorFormData {
    result: number
    username?: string
    createdAt: Date
    previousResults?: CalculatorResult[]
}

const CalculatorFormAndResult = () => {
    const [result, setResult] = useState<CalculatorResult>()

    const methods = useForm<CalculatorFormData>({
        resolver: yupResolver(calculatorYupSchema),
        mode: "onChange",
        defaultValues: {
            operation: "PLUS"
        },
    })

    const {handleSubmit, formState, register} = methods

    const onSubmit = async (formData: CalculatorFormData) => {
        const {firstValue, secondValue, operation} = formData

        getSession()
            .then(session => (session as CustomSession)?.user)
            .then(sessionUser => hasRole("ROLE_USER", sessionUser)
                ? `/api/calculator?first=${firstValue}&second=${secondValue}&operation=${operation}`
                : `${CALC_BACKEND_BASE_URL}/${firstValue}/${secondValue}/${operation}`
            )
            .then(url => fetch(url))
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
                    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 max-w-md">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            <ValidatedTextInput label="First Value" name="firstValue"/>
                            <ValidatedTextInput label="Second Value" name="secondValue"/>

                            <input type="hidden" {...register("operation")} />

                            <Button
                                type="submit"
                                disabled={!formState.isValid}
                                onClick={() => methods.setValue("operation", "PLUS")}
                            >Add</Button>
                            <Button
                                type="submit"
                                disabled={!formState.isValid}
                                onClick={() => methods.setValue("operation", "MINUS")}
                            >Subtract</Button>
                        </div>
                    </form>
                </FormProvider>
            </div>

            {result && (
                <div className="mt-6 p-6 bg-gray-50 border rounded-lg shadow-sm">
                    <h2 className="text-xl font-semibold text-gray-800 text-center flex items-center justify-center gap-2">
                        <span className="text-green-500">✓</span>
                        Calculation Result
                    </h2>
                    <div className="mt-4 flex justify-center items-baseline text-3xl font-bold text-gray-700 ">
                        <span>{result.firstValue}</span>
                        <span className="mx-2 text-gray-500">
                            {result.operation === 'PLUS' ? '+' : '-'}
                        </span>
                        <span>{result.secondValue}</span>
                        <span className="mx-2 text-gray-500">=</span>
                        <span className="text-green-600">{result.result}</span>
                    </div>

                    {result.previousResults && result.previousResults.length > 0 && (
                        <div className="mt-8">
                            <h3 className="text-md font-semibold text-gray-700 text-center">
                                Previous results on this server
                            </h3>
                            <table className="min-w-full bg-white mt-2 border border-gray-300">
                                <thead>
                                <tr>
                                    <th className="py-2 px-4 border-b text-left text-gray-700">Calculation</th>
                                    <th className="py-2 px-4 border-b text-left text-gray-700">User</th>
                                    <th className="py-2 px-4 border-b text-left text-gray-700">Date</th>
                                </tr>
                                </thead>
                                <tbody>
                                {result.previousResults.map((prevResult, index) => (
                                    <tr key={index} className="hover:bg-gray-50">
                                        <td className="py-2 px-4 border-b text-gray-600 text-left">
                                            {prevResult.firstValue} {prevResult.operation === 'PLUS' ? '+' : '-'} {prevResult.secondValue} = {prevResult.result}
                                        </td>
                                        <td className="py-2 px-4 border-b text-gray-600 text-left">
                                            {prevResult.username || "Anonymous"}
                                        </td>
                                        <td className="py-2 px-4 border-b text-gray-600 text-left">
                                            {new Intl.DateTimeFormat('en-US', {
                                                dateStyle: 'medium',
                                                timeStyle: 'short'
                                            }).format(new Date(prevResult.createdAt))}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}

export default CalculatorFormAndResult