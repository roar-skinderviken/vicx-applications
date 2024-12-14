"use client"

import {useCallback, useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {InferType} from "yup"
import {getSession} from "next-auth/react"
import {hasOneOfRoles} from "@/utils/authUtils"
import PreviousCalculations from "@/app/tomcat/PreviousCalculations"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import {extractUserOrSignOut} from "@/auth/tokenUtils"

// put this in next-app/.env.local
// NEXT_PUBLIC_CALCULATOR_BACKEND_URL=http://localhost:8080/api/calculator
export const CALC_BACKEND_BASE_URL = process.env.NEXT_PUBLIC_CALCULATOR_BACKEND_URL || "/backend-spring-boot/api/calculator"
export const CALC_NEXT_BACKEND_URL = "/api/user/calculator"

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
    const [currentPreviousResultPage, setCurrentPreviousResultPage] = useState(0)
    const [hasMorePreviousResults, setHasMorePreviousResults] = useState(false)
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

    const fetchPreviousCalculations = (pageNumber: number) => {
        fetch(`${CALC_BACKEND_BASE_URL}?page=${pageNumber}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error(
                        "Network response was not ok",
                        {cause: response.status}
                    )
                }
                return response.json()
            })
            .then(data => {
                setHasMorePreviousResults(data.page.number < data.page.totalPages - 1)
                setPreviousResults(prev => [...prev, ...data.content])
            })
    }

    const onFetchMore = () => {
        fetchPreviousCalculations(currentPreviousResultPage + 1)
        setCurrentPreviousResultPage(prev => prev + 1)
    }

    const onDeleteCallback = useCallback((idsToDelete: number[]) => {
        setCurrentPreviousResultPage(0)
        setPreviousResults([])
        getSession()
            .then(extractUserOrSignOut)
            .then(sessionUser => {
                    const hasUserRole = hasOneOfRoles(["USER", "GITHUB_USER"], sessionUser)
                    if (!hasUserRole) {
                        throw new Error('User not allowed to perform this operation')
                    }
                    return CALC_NEXT_BACKEND_URL
                }
            )
            .then(url => fetch(url, {method: "DELETE", body: JSON.stringify(idsToDelete)}))
            .then(() => fetchPreviousCalculations(0))
    }, [])

    const onSubmit = async (formData: CalculatorFormData) => {
        setIsLoading(true)
        setResult(undefined)
        setCurrentPreviousResultPage(0)
        setPreviousResults([])

        getSession()
            .then(extractUserOrSignOut)
            .then(sessionUser => {
                    const hasUserRole = hasOneOfRoles(["USER", "GITHUB_USER"], sessionUser)
                    if (hasUserRole) {
                        setUsername(sessionUser.id || undefined)
                    }

                    return hasUserRole ? CALC_NEXT_BACKEND_URL : CALC_BACKEND_BASE_URL
                }
            )
            .then(url => fetch(
                url,
                {
                    method: "POST",
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(formData),
                }
            ))
            .then(response => {
                if (!response.ok) {
                    throw new Error(
                        "Network response was not ok",
                        {cause: response.status}
                    )
                }
                return response.json()
            })
            .then(data => setResult(data))
            .then(() => fetchPreviousCalculations(0))
            .catch(error => console.debug("Error:", error))
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

                            <ButtonWithSpinner
                                disabled={!formState.isValid || isLoading}
                                buttonText="Add"
                                isLoading={isLoading && "PLUS" === operationFromForm}
                                onClick={() => methods.setValue("operation", "PLUS")}/>

                            <ButtonWithSpinner
                                disabled={!formState.isValid || isLoading}
                                buttonText="Subtract"
                                isLoading={isLoading && "MINUS" === operationFromForm}
                                onClick={() => methods.setValue("operation", "MINUS")}/>
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
                            hasMorePages={hasMorePreviousResults}
                            onFetchMore={onFetchMore}
                        />
                    )}
                </div>
            )}
        </div>
    )
}

export default CalculatorFormAndResult