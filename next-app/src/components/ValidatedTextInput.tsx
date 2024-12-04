"use client"

import {Label, TextInput} from "flowbite-react"
import {useFormContext} from "react-hook-form"
import {HiCheck, HiExclamationCircle} from "react-icons/hi"

const InvisibleIcon = () => (
    <svg width="1em" height="1em" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"
         style={{visibility: 'hidden'}}>
        <path d="M0 0h24v24H0z" fill="none"/>
    </svg>
)

const ValidatedTextInput = ({name, label, type = "text", errorMessage}: {
    name: string
    label?: string
    type?: string,
    errorMessage?: string
}) => {
    const {
        register,
        watch,
        formState: {
            errors
        }
    } = useFormContext()
    const value = watch(name)

    let helperText

    if (errorMessage) {
        helperText = <span className="font-medium text-left block">{errorMessage}</span>
    } else if (errors[name]) {
        const yupValidationErrorMessage = (errors[name] as { message?: string }).message
        helperText = <span className="font-medium text-left block">{yupValidationErrorMessage}</span>
    }

    return (
        <div className="flex flex-col">
            {label && <Label
                htmlFor={name}
                className={`mb-1 text-left ${errors[name] ? "text-red-500" : "text-gray-700"}`}
                color={errors[name] ? "failure" : "success"}
                value={label}
            />}
            <TextInput
                id={name}
                data-testid={`${name}-input`}
                {...register(name)}
                color={errors[name] ? "failure" : "success"}
                rightIcon={
                    helperText
                        ? HiExclamationCircle
                        : value ? HiCheck : InvisibleIcon
                }
                type={type}
                sizing="md"
                helperText={helperText}
            />
        </div>
    )
}

export default ValidatedTextInput