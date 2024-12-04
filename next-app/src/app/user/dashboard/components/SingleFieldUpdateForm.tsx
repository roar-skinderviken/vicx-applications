"use client"

import React, {useState, useRef, useEffect} from "react"
import {DefaultValues, FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import {Button, Card} from "flowbite-react"
import * as yup from "yup"
import {getSession} from "next-auth/react"
import {extractUserOrSignOut} from "@/auth/tokenUtils"

type SingleFieldUpdateFormProps<T extends yup.AnyObjectSchema> = {
    schema: T
    defaultValues: DefaultValues<yup.InferType<T>>
    fields: Array<{
        name: keyof yup.InferType<T>
        label: string
        type?: "text" | "password"
    }>
    endpoint: string
    onUpdateSuccess: (message: string) => void
    onCancel: () => void
    resetFormAfterSubmit?: boolean
}

const SingleFieldUpdateForm = <T extends yup.AnyObjectSchema>({
                                                                  schema,
                                                                  defaultValues,
                                                                  fields,
                                                                  endpoint,
                                                                  onUpdateSuccess,
                                                                  onCancel
                                                              }: SingleFieldUpdateFormProps<T>) => {
    const [isLoading, setIsLoading] = useState(false)
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>()

    const methods = useForm<yup.InferType<T>>({
        resolver: yupResolver(schema),
        mode: "onChange",
        defaultValues,
    })

    const {handleSubmit, watch, formState} = methods
    const watchedValues = watch()

    const prevWatchedValuesRef = useRef<yup.InferType<T>>(defaultValues)

    const isSubmitDisabled =
        !formState.isValid || JSON.stringify(watchedValues) === JSON.stringify(defaultValues)

    useEffect(() => {
        if (JSON.stringify(watchedValues) !== JSON.stringify(prevWatchedValuesRef.current)) {
            setValidationErrors(undefined)
            prevWatchedValuesRef.current = watchedValues
        }
    }, [watchedValues])

    const onSubmit = async (formData: yup.InferType<T>) => {
        setIsLoading(true)

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
            .then((data) => onUpdateSuccess(data))
            .catch(error => {
                if (error.validationErrors) setValidationErrors(error.validationErrors)
            })
            .finally(() => setIsLoading(false))
    }

    return (
        <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
            <FormProvider {...methods}>
                <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
                    <div className="flex items-start gap-2">
                        {fields.map((field) => (
                            <div key={String(field.name)} className="flex flex-col">
                                <ValidatedTextInput
                                    name={String(field.name)}
                                    type={field.type || "text"}
                                    errorMessage={validationErrors?.[field.name as string]}
                                />
                            </div>
                        ))}

                        <div className="flex items-center gap-2">
                            <ButtonWithSpinner
                                buttonText="Save"
                                isLoading={isLoading}
                                disabled={isSubmitDisabled}
                                className="w-24"
                            />
                            <Button onClick={onCancel}>Cancel</Button>
                        </div>
                    </div>

                </form>
            </FormProvider>
        </Card>
    )
}

export default SingleFieldUpdateForm
