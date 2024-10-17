"use client"

import {useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import ValidatedInput from "@/components/ValidatedInput"
import {Button} from "flowbite-react"

//const BACKEND_BASE_URL = "http://localhost:8080/api"
const BACKEND_BASE_URL = "/sample/api"

const schema = yup.object().shape({
    firstValue: yup
        .number()
        .typeError("First value must be a number")
        .required("First value is required"),
    secondValue: yup
        .number()
        .typeError("Second value must be a number")
        .required("Second value is required")
})

const CalculatorFormAndResult = () => {
    const [result, setResult] = useState()

    const methods = useForm({
        resolver: yupResolver(schema),
        mode: "onChange"
    })

    const {handleSubmit, formState, register} = methods

    const onSubmit = (formData) => {
        const {firstValue, secondValue, operation} = formData

        fetch(`${BACKEND_BASE_URL}/${firstValue}/${secondValue}/${operation}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok')
                }
                return response.json()
            })
            .then(data => setResult(data))
            .catch(error => console.error("Error:", error))
    }

    // noinspection JSCheckFunctionSignatures,JSUnresolvedReference
    return (
        <div className="container mx-auto my-10 text-center">
            <div className="flex justify-center">
                <FormProvider {...methods}>
                    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 max-w-md">
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            <ValidatedInput label="First Value" name="firstValue"/>
                            <ValidatedInput label="Second Value" name="secondValue"/>

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
                        {result.firstValue} {result.operation === 'PLUS' ? '+' : '-'} {result.secondValue} =
                        <span className="font-semibold text-cyan-600"> {result.result}</span>
                    </p>
                </div>
            )}
        </div>
    )
}

export default CalculatorFormAndResult