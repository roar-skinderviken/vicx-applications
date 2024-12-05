"use client"

import {emailAddressSchema} from "@/utils/yupSharedSchemas"
import UpdateForm from "@/app/user/dashboard/components/UpdateForm"

const UpdateEmailAddressForm = ({currentEmailAddress, onUpdateSuccess, onCancel}: {
    currentEmailAddress: string
    onUpdateSuccess: (message: string) => void
    onCancel: () => void
}) => {
    return <UpdateForm
        schema={emailAddressSchema}
        defaultValues={{email: currentEmailAddress}}
        fields={[{name: "email", label: "Email", type: "text"}]}
        endpoint="/api/user"
        onUpdateSuccess={onUpdateSuccess}
        onCancel={onCancel}/>
}

export default UpdateEmailAddressForm
