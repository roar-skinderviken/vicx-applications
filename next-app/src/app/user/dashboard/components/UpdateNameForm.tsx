"use client"

import {nameSchema} from "@/utils/yupSharedSchemas"
import UpdateForm from "@/app/user/dashboard/components/UpdateForm"

const UpdateNameForm = ({currentName, onUpdateSuccess, onCancel}: {
    currentName: string
    onUpdateSuccess: (message: string) => void
    onCancel: () => void
}) => {
    return <UpdateForm
        schema={nameSchema}
        defaultValues={{name: currentName}}
        fields={[{name: "name", label: "Name", type: "text"}]}
        endpoint="/api/user"
        onUpdateSuccess={onUpdateSuccess}
        onCancel={onCancel}/>
}

export default UpdateNameForm
