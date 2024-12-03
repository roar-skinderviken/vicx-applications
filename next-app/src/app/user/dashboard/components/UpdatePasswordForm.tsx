"use client"

import * as yup from "yup"
import UpdateForm from "@/app/user/dashboard/components/UpdateForm"

const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/

const updatePasswordSchema = yup.object({
    currentPassword: yup.string().trim()
        .required("Current password is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`),

    newPassword: yup
        .string()
        .required("Password is required")
        .min(8, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`)
        .matches(
            PASSWORD_REGEX,
            "Password must have at least one uppercase, one lowercase letter, and one number"
        ),

    confirmPassword: yup
        .string()
        .oneOf([yup.ref("newPassword")], "Passwords must match")
        .required("Confirm password is required"),
})

const UpdatePasswordForm = ({cardTitle}: { cardTitle: string }) => {
    return (
        <UpdateForm
            cardTitle={cardTitle}
            schema={updatePasswordSchema}
            defaultValues={{currentPassword: "", newPassword: "", confirmPassword: ""}}
            fields={[
                {name: "currentPassword", label: "Current Password", type: "password"},
                {name: "newPassword", label: "New Password", type: "password"},
                {name: "confirmPassword", label: "Confirm Password", type: "password"},
            ]}
            endpoint="/api/user/password"
            resetFormAfterSubmit={true}
        />
    )
}

export default UpdatePasswordForm
