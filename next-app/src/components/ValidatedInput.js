"use client"

import {Label, TextInput} from "flowbite-react"
import {useFormContext} from "react-hook-form"

const ValidatedInput = ({name, label, type = "text", defaultValue = undefined}) => {
    const {register, formState: {errors}} = useFormContext()

    return (
        <div className="flex flex-col">
            <Label
                htmlFor={name}
                className={`mb-1 text-left ${errors[name] ? "text-red-500" : "text-gray-700"}`}
                color={errors[name] ? "failure" : "success"}
                value={label}
            />
            <TextInput
                type={type}
                id={name}
                {...register(name)}
                color={errors[name] ? "failure" : "success"}
                defaultValue={defaultValue}
                className="block w-full rounded focus:outline-none focus:ring-2 focus:ring-cyan-500"
                helperText={errors[name] && (
                    <span className="font-medium">{errors[name].message}</span>
                )}
            />
        </div>
    )
}

export default ValidatedInput