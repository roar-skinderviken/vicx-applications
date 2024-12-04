"use client"

import SingleFieldUpdateForm from "@/app/user/dashboard/components/SingleFieldUpdateForm"
import {emailAddressSchema} from "@/utils/yupSharedSchemas"

const UpdateEmailAddressForm = ({currentEmailAddress, onUpdateSuccess, onCancel}: {
    currentEmailAddress: string
    onUpdateSuccess: (message: string) => void
    onCancel: () => void
}) => {
    return <SingleFieldUpdateForm
        schema={emailAddressSchema}
        defaultValues={{email: currentEmailAddress}}
        fields={[{name: "email", label: "Email", type: "text"}]}
        endpoint="/api/user"
        onUpdateSuccess={onUpdateSuccess}
        onCancel={onCancel}/>
}

export default UpdateEmailAddressForm
