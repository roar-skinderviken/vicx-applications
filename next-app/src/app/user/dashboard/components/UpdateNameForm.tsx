"use client"

import SingleFieldUpdateForm from "@/app/user/dashboard/components/SingleFieldUpdateForm"
import {nameSchema} from "@/utils/yupSharedSchemas"

const UpdateNameForm = ({currentName, onUpdateSuccess, onCancel}: {
    currentName: string
    onUpdateSuccess: (message: string) => void
    onCancel: () => void
}) => {
    return <SingleFieldUpdateForm
        schema={nameSchema}
        defaultValues={{name: currentName}}
        fields={[{name: "name", label: "Name", type: "text"}]}
        endpoint="/api/user"
        onUpdateSuccess={onUpdateSuccess}
        onCancel={onCancel}/>
}

export default UpdateNameForm