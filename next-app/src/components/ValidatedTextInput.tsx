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

const ValidatedTextInput = ({name, label, defaultValue}: {
    name: string,
    label: string,
    defaultValue?: string
}) => {
    const {register, watch, formState: {errors}} = useFormContext()
    const value = watch(name)

    return (
        <div className="flex flex-col">
            <Label
                htmlFor={name}
                className={`mb-1 text-left ${errors[name] ? "text-red-500" : "text-gray-700"}`}
                color={errors[name] ? "failure" : "success"}
                value={label}
            />
            <TextInput
                id={name}
                {...register(name)}
                color={errors[name] ? "failure" : "success"}
                rightIcon={
                    errors[name]
                        ? HiExclamationCircle
                        : value ? HiCheck : InvisibleIcon
                }
                defaultValue={defaultValue}
                type="text"
                sizing="md"
                helperText={errors[name] && (
                    <span className="font-medium">
                        {(errors[name] as { message?: string }).message}
                    </span>
                )}
            />
        </div>
    )
}

export default ValidatedTextInput