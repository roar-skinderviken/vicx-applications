"use client"

import {useCallback, useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {InferType} from "yup"
import {getSession} from "next-auth/react"
import {CustomSession} from "@/types/authTypes"
import {hasRole} from "@/utils/authUtils"
import PreviousCalculations from "@/app/tomcat/PreviousCalculations"
import SubmitButtonWithSpinner from "@/app/tomcat/SubmitButtonWithSpinner"

// put this in next-app/.env.local
// NEXT_PUBLIC_TOMCAT_BACKEND_URL=http://localhost:8080/api
export const CALC_BACKEND_BASE_URL = process.env.NEXT_PUBLIC_CALCULATOR_BACKEND_URL || "/backend-spring-boot/api/calculator"
export const CALC_NEXT_BACKEND_URL = "/api/calculator"

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

export interface CalculationResult extends CalculatorFormData {
    id: number
    result: number
    username?: string
    createdAt: Date
}

const CalculatorFormAndResult = () => {
    const [result, setResult] = useState<CalculationResult>()
    const [username, setUsername] = useState<string>()
    const [previousResults, setPreviousResults] = useState<CalculationResult[]>([])
    const [isLoading, setIsLoading] = useState<boolean>(false)

    const methods = useForm<CalculatorFormData>({
        resolver: yupResolver(calculatorYupSchema),
        mode: "onChange",
        defaultValues: {
            operation: "PLUS"
        },
    })

    const {handleSubmit, formState, register, watch} = methods
    const operationFromForm = watch("operation")

    const fetchPreviousCalculations = () => {
        fetch(CALC_BACKEND_BASE_URL)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok')
                }
                return response.json()
            })
            .then(data => setPreviousResults(data))
            .catch(error => console.error("Error:", error))
    }

    const onDeleteCallback = useCallback((idsToDelete: number[]) => {
        getSession()
            .then(session => (session as CustomSession)?.user)
            .then(sessionUser => {
                    const hasUserRole = hasRole("ROLE_USER", sessionUser)
                    if (!hasUserRole) {
                        throw new Error('User not allowed to perform this operation')
                    }
                    return CALC_NEXT_BACKEND_URL
                }
            )
            .then(url => fetch(url, {method: "DELETE", body: JSON.stringify(idsToDelete)}))
            .then(() => fetchPreviousCalculations())
    }, [])

    const onSubmit = async (formData: CalculatorFormData) => {
        setIsLoading(true)
        setResult(undefined)

        getSession()
            .then(session => (session as CustomSession)?.user)
            .then(sessionUser => {
                    const hasUserRole = hasRole("ROLE_USER", sessionUser)
                    if (hasUserRole) {
                        setUsername(sessionUser.name || undefined)
                    }

                    return hasUserRole ? CALC_NEXT_BACKEND_URL : CALC_BACKEND_BASE_URL
                }
            )
            .then(url => fetch(
                url,
                {
                    method: "POST",
                    body: JSON.stringify(formData),
                    headers: {'Content-Type': 'application/json'}
                }
            ))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok')
                }
                return response.json()
            })
            .then(data => setResult(data))
            .catch(error => console.error("Error:", error))
            .then(() => fetchPreviousCalculations())
            .finally(() => setIsLoading(false))
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

                            <SubmitButtonWithSpinner
                                disabled={!formState.isValid || isLoading}
                                buttonText="Add"
                                isLoading={isLoading && "PLUS" === operationFromForm}
                                onButtonClick={() => methods.setValue("operation", "PLUS")}/>

                            <SubmitButtonWithSpinner
                                disabled={!formState.isValid || isLoading}
                                buttonText="Subtract"
                                isLoading={isLoading && "MINUS" === operationFromForm}
                                onButtonClick={() => methods.setValue("operation", "MINUS")}/>
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

                    {previousResults.length > 0 && (
                        <PreviousCalculations
                            username={username}
                            calculations={previousResults}
                            onDelete={onDeleteCallback}
                        />
                    )}
                </div>
            )}
        </div>
    )
}

export default CalculatorFormAndResult