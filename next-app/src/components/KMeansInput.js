"use client"

import {Label, TextInput} from "flowbite-react"
import {useFormContext} from "react-hook-form"

const KMeansInput = ({name, label, type = "text", defaultValue = undefined}) => {
    const {register, formState: {errors}} = useFormContext()

    return (
        <div>
            <div className="mb-2 block">
                <Label
                    htmlFor={name}
                    color={errors[name] ? "failure" : "success"}
                    value={label}
                />
            </div>
            <TextInput
                type={type}
                id={name}
                {...register(name, {required: true})}
                color={errors[name] ? "failure" : "success"}
                defaultValue={defaultValue}
                helperText={errors[name] && (
                    <span className="font-medium">{errors[name].message}</span>
                )}
            />
        </div>
    )
}

export default KMeansInput