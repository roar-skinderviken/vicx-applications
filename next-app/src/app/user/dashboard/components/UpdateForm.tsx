"use client"

import React, {useState, useRef, useEffect} from "react"
import {DefaultValues, FormProvider, useForm, useWatch} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {Card, Button} from "flowbite-react"
import {getSession} from "next-auth/react"
import {extractUserOrSignOut} from "@/auth/tokenUtils"
import * as yup from "yup"

type UpdateFormProps<T extends yup.AnyObjectSchema> = {
    schema: T
    defaultValues: DefaultValues<yup.InferType<T>>
    fields: Array<{
        name: keyof yup.InferType<T>
        label: string
        type?: "text" | "password"
    }>
    endpoint: string
    cardTitle?: string
    onUpdateSuccess?: (message: string) => void
    onCancel?: () => void
    resetFormAfterSubmit?: boolean
    showSuccessMessage?: boolean
}

const UpdateForm = <T extends yup.AnyObjectSchema>({
                                                       schema,
                                                       defaultValues,
                                                       fields,
                                                       endpoint,
                                                       cardTitle,
                                                       onUpdateSuccess,
                                                       onCancel,
                                                       resetFormAfterSubmit = false,
                                                       showSuccessMessage = true,
                                                   }: UpdateFormProps<T>) => {
    const [isLoading, setIsLoading] = useState(false)
    const [result, setResult] = useState<string>()
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>()

    const methods = useForm<yup.InferType<T>>({
        resolver: yupResolver(schema),
        mode: "onChange",
        defaultValues,
    })

    const {handleSubmit, formState} = methods
    const watchedValues = useWatch({
        control: methods.control,
        name: undefined,
    })
    const prevWatchedValuesRef = useRef<yup.InferType<T>>(defaultValues)

    const isSubmitDisabled =
        !formState.isValid || JSON.stringify(watchedValues) === JSON.stringify(defaultValues)

    useEffect(() => {
        if (JSON.stringify(watchedValues) !== JSON.stringify(prevWatchedValuesRef.current)) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setValidationErrors(undefined)
            prevWatchedValuesRef.current = watchedValues
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
                if (!response.ok) throw await response.json()
                return response.text()
            })
            .then((data) => {
                if (onUpdateSuccess) {
                    onUpdateSuccess(data)
                } else {
                    setResult(data)
                }
                if (resetFormAfterSubmit) methods.reset(defaultValues)
            })
            .catch((error) => {
                if (error.validationErrors) setValidationErrors(error.validationErrors)
            })
            .finally(() => setIsLoading(false))
    }

    return (
        <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
            {result && showSuccessMessage && (
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
            {cardTitle && <h5 className="text-2xl tracking-tight text-gray-900 dark:text-white">{cardTitle}</h5>}
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
                    <div className="flex items-center justify-center gap-2">
                        <ButtonWithSpinner
                            buttonText={onUpdateSuccess ? "Save" : "Submit"}
                            isLoading={isLoading}
                            disabled={isSubmitDisabled}
                            className="w-24"
                        />
                        {onCancel && <Button onClick={onCancel}>Cancel</Button>}
                    </div>
                </form>
            </FormProvider>
        </Card>
    )
}

export default UpdateForm
