"use client"

import React, {useState, useEffect, useRef} from "react"
import {DefaultValues, FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {getSession} from "next-auth/react"
import {extractUserOrSignOut} from "@/auth/tokenUtils"
import * as yup from "yup"
import {Card} from "flowbite-react"

type UpdateFormProps<T extends yup.AnyObjectSchema> = {
    schema: T
    defaultValues: DefaultValues<yup.InferType<T>>
    fields: Array<{
        name: keyof yup.InferType<T>
        label: string
        type?: "text" | "password"
    }>
    endpoint: string,
    cardTitle: string,
    resetFormAfterSubmit?: boolean,
}

const UpdateForm = <T extends yup.AnyObjectSchema>({
                                                       schema,
                                                       defaultValues,
                                                       fields,
                                                       endpoint,
                                                       cardTitle,
                                                       resetFormAfterSubmit = false
                                                   }: UpdateFormProps<T>) => {

    const [isLoading, setIsLoading] = useState(false)
    const [result, setResult] = useState<string>()
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>()

    const methods = useForm<yup.InferType<T>>({
        resolver: yupResolver(schema),
        mode: "onChange",
        defaultValues,
    })

    const {handleSubmit, formState, watch} = methods

    const watchedValues = watch()
    const prevWatchedValuesRef = useRef<yup.InferType<T>>(defaultValues)

    const isSubmitDisabled = !formState.isValid
        || JSON.stringify(watchedValues) === JSON.stringify(defaultValues)

    useEffect(() => {
        // Only reset validation errors when watched values change significantly
        if (JSON.stringify(watchedValues) !== JSON.stringify(prevWatchedValuesRef.current)) {
            setValidationErrors(undefined)
            prevWatchedValuesRef.current = watchedValues // Update the ref to track the latest values
        }
    }, [watchedValues])

    const onSubmit = async (formData: yup.InferType<T>) => {
        setIsLoading(true)
        setResult(undefined)

        getSession()
            .then(extractUserOrSignOut)
            .then(() =>
                fetch(endpoint, {
                    method: "PATCH",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify(formData),
                })
            )
            .then(async (response) => {
                if (!response.ok) {
                    throw await response.json()
                }
                return response.text()
            })
            .then((data) => {
                setResult(data)
                if (resetFormAfterSubmit) methods.reset(defaultValues)
            })
            .catch(error => {
                if (error.validationErrors) {
                    setValidationErrors(error.validationErrors)
                }
            })
            .finally(() => setIsLoading(false))
    }

    return (
        <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
            {result && (
                <>
                    <h2 className="text-xl font-semibold text-gray-800 text-center flex items-center justify-center gap-2">
                        <span className="text-green-500">✓</span>
                        {result}
                    </h2>
                    <p className="text-center text-gray-700">
                        Please log out and log back in to see the changes take effect.
                    </p>
                </>
            )}
            <h5 className="text-2xl tracking-tight text-gray-900 dark:text-white">{cardTitle}</h5>
            <FormProvider {...methods}>
                <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
                    {fields.map((field) => (
                        <ValidatedTextInput
                            key={String(field.name)}
                            label={field.label}
                            name={String(field.name)}
                            type={field.type || "text"}
                            errorMessage={validationErrors?.[field.name as string]}
                        />
                    ))}
                    <ButtonWithSpinner
                        disabled={isSubmitDisabled}
                        buttonText="Submit"
                        isLoading={isLoading}
                        className="col-span-1 mt-2 w-full"
                    />
                </form>
            </FormProvider>
        </Card>
    )
}

export default UpdateForm
