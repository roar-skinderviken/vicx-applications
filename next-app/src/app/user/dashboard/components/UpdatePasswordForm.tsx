"use client"

import * as yup from "yup"
import UpdateForm from "@/app/user/dashboard/components/UpdateForm"
import {passwordSchema} from "@/utils/yupSharedSchemas"

const updatePasswordSchema = yup.object({
    currentPassword: yup.string().trim()
        .required("Current password is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`),

    password: yup.reach(passwordSchema, "password"),
    confirmPassword: yup.reach(passwordSchema, "confirmPassword")
})

const UpdatePasswordForm = ({cardTitle}: { cardTitle: string }) => {
    return (
        <UpdateForm
            cardTitle={cardTitle}
            schema={updatePasswordSchema}
            defaultValues={{currentPassword: "", password: "", confirmPassword: ""}}
            fields={[
                {name: "currentPassword", label: "Current Password", type: "password"},
                {name: "password", label: "New Password", type: "password"},
                {name: "confirmPassword", label: "Confirm Password", type: "password"},
            ]}
            endpoint="/api/user/password"
            resetFormAfterSubmit={true}
        />
    )
}

export default UpdatePasswordForm
