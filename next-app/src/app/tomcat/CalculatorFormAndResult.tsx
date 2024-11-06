"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {Button} from "flowbite-react"
import {InferType} from "yup"

// put this in next-app/.env.local
// NEXT_PUBLIC_TOMCAT_BACKEND_URL=http://localhost:8080/api
const BACKEND_BASE_URL = process.env.NEXT_PUBLIC_TOMCAT_BACKEND_URL || "/backend-spring-boot/api/calculator"

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
}

const CalculatorFormAndResult = ({useSecureEndpoint = false}: { useSecureEndpoint?: boolean }) => {
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

        const requestUrl = useSecureEndpoint
            ? `/api/calculator?first=${firstValue}&second=${secondValue}&operation=${operation}`
            : `${BACKEND_BASE_URL}/${firstValue}/${secondValue}/${operation}`

        fetch(requestUrl)
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
                <div className="mt-6 p-4 bg-gray-100 rounded shadow-md">
                    <h2 className="text-lg font-bold text-gray-700">Result</h2>
                    <p className="text-base text-gray-600 mt-2">
                        {result.firstValue} {result.operation === 'PLUS' ? '+' : '-'} {result.secondValue} = {result.result}
                    </p>
                </div>
            )}
        </div>
    )
}

export default CalculatorFormAndResult