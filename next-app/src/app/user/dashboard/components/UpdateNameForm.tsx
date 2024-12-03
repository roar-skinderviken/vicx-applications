"use client"

import * as yup from "yup"
import SingleFieldUpdateForm from "@/app/user/dashboard/components/SingleFieldUpdateForm"

export const updateNameSchema = yup.object({
    name: yup
        .string()
        .trim()
        .required("Name is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`),
})

const UpdateNameForm = ({currentName, onUpdateSuccess, onEndEdit}: {
    currentName: string
    onUpdateSuccess?: () => void
    onEndEdit: () => void
}) => {
    return (
        <SingleFieldUpdateForm
            schema={updateNameSchema}
            defaultValues={{name: currentName}}
            fields={[{name: "name", label: "Name", type: "text"}]}
            endpoint="/api/user"
            onUpdateSuccess={onUpdateSuccess}
            onEndEdit={onEndEdit}
        />
    )
}

export default UpdateNameForm
