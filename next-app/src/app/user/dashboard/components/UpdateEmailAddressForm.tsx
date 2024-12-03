"use client"

import * as yup from "yup"
import SingleFieldUpdateForm from "@/app/user/dashboard/components/SingleFieldUpdateForm"

export const updateEmailAddressSchema = yup.object({
    email: yup
        .string()
        .trim()
        .required("Email is required")
        .email("Please enter a valid email address")
})

const UpdateEmailAddressForm = ({currentEmailAddress, onUpdateSuccess, onEndEdit}: {
    currentEmailAddress: string
    onUpdateSuccess?: () => void
    onEndEdit: () => void
}) => {
    return (
        <SingleFieldUpdateForm
            schema={updateEmailAddressSchema}
            defaultValues={{email: currentEmailAddress}}
            fields={[{name: "email", label: "Email", type: "text"}]}
            endpoint="/api/user"
            onUpdateSuccess={onUpdateSuccess}
            onEndEdit={onEndEdit}
        />
    )
}

export default UpdateEmailAddressForm
