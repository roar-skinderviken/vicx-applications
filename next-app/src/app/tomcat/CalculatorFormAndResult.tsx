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
import {createClient, gql, fetchExchange} from "urql"

export const CALC_PUBLIC_GRAPHQL_URL = "/api/graphql/calculator"
export const CALC_RESTRICTED_GRAPHQL_URL = "/api/user/calculator"

enum CalculatorOperation {
    PLUS = 'PLUS',
    MINUS = 'MINUS'
}

export const createCalculationQuery = gql`
    mutation($firstValue: Int!, $secondValue: Int!, $operation: CalculatorOperation!) {
        createCalculation(firstValue: $firstValue, secondValue: $secondValue, operation: $operation) {
            firstValue
            secondValue
            operation
            result
        }
    }`

export const previousResultsQuery = gql`
    query($page: Int!) {
        getAllCalculations(page: $page) {
            page
            totalPages
            calculations {
                id
                firstValue
                secondValue
                operation
                result
                username
                createdAt
            }
        }
    }`

export const deleteCalculationsQuery = gql`
    mutation($ids: [Int!]!) {
        deleteCalculations(ids: $ids)
    }`

const publicApiClient = () => createClient({
    url: CALC_PUBLIC_GRAPHQL_URL,
    exchanges: [fetchExchange]
})

const restrictedApiClient = () => createClient({
    url: CALC_RESTRICTED_GRAPHQL_URL,
    exchanges: [fetchExchange]
})

const calculatorYupSchema = yup.object({
    firstValue: yup
        .number()
        .typeError("First value must be a number")
        .required("First value is required"),
    secondValue: yup
        .number()
        .typeError("Second value must be a number")
        .required("Second value is required"),
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
        publicApiClient()
            .query(previousResultsQuery, {page: pageNumber}).toPromise()
            .then(response => response.data)
            .then(data => {
                const innerResult = data.getAllCalculations
                setHasMorePreviousResults(innerResult.page < innerResult.totalPages - 1)
                setPreviousResults(prev => [...prev, ...innerResult.calculations])
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
                    return restrictedApiClient()
                }
            )
            .then(client => client.mutation(deleteCalculationsQuery, {ids: idsToDelete}))
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

                    return hasUserRole
                        ? restrictedApiClient()
                        : publicApiClient()
                }
            )
            .then(client => client
                .mutation(createCalculationQuery, {
                    firstValue: formData.firstValue,
                    secondValue: formData.secondValue,
                    operation: formData.operation as CalculatorOperation
                })
                .toPromise()
            )
            .then(response => response.data)
            .then(data => setResult(data.createCalculation))
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