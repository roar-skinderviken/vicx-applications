"use client"

import React, {useEffect, useState} from "react"
import {InferType} from "yup"
import {MAX_IMAGE_FILE_SIZE, SUPPORTED_IMAGE_FORMATS} from "@/utils/yupSharedSchemas"
import {FormProvider, useForm, useWatch} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import Image from "next/image"
import {Button, Card, FileInput} from "flowbite-react"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import * as yup from "yup"
import {getSession} from "next-auth/react"
import {extractUserOrSignOut} from "@/auth/tokenUtils"
import {useDropzone} from "react-dropzone"

const BACKEND_URL = "/api/user/image"

const profileImageUploadSchema = yup.object({
    image: yup.mixed<FileList>()
        .test(
            "required",
            "File is required",
            (files: FileList | undefined) => {
                return files?.[0] !== undefined
            })
        .test(
            "file-type",
            "Only PNG and JPG files are allowed",
            (files: FileList | undefined) => {
                const file = files?.[0]
                return file
                    ? SUPPORTED_IMAGE_FORMATS.includes(file.type)
                    : true
            })
        .test(
            "file-size",
            `File size must not exceed ${MAX_IMAGE_FILE_SIZE / 1024} KB`,
            (files: FileList | undefined) => {
                const file = files?.[0]
                return file
                    ? file.size < MAX_IMAGE_FILE_SIZE
                    : true
            }
        )
        .required()
})

type ProfileImageData = InferType<typeof profileImageUploadSchema>

const UpdateProfileImageForm = ({onUploadSuccess, onCancel}: {
    onUploadSuccess: (message: string) => void
    onCancel: () => void
}) => {
    const [profileImage, setProfileImage] = useState<string | null>()
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>()

    const methods = useForm<ProfileImageData>({
        resolver: yupResolver(profileImageUploadSchema),
        mode: "onChange"
    })

    const {
        handleSubmit,
        formState,
        formState: {
            errors
        },
        register,
        setValue,
        trigger
    } = methods

    const watchedFile = useWatch({
        control: methods.control,
        name: "image"
    })

    useEffect(() => {
        if (errors.image) return

        if (watchedFile?.[0]) {
            const file = watchedFile[0]
            const reader = new FileReader()
            reader.onload = () => setProfileImage(reader.result as string)
            reader.readAsDataURL(file)
        }
    }, [watchedFile, errors.image])

    const onSubmit = async (formData: ProfileImageData) => {
        setValidationErrors(undefined)
        setIsLoading(true)

        const multipartFormData = new FormData()
        multipartFormData.append("image", formData.image?.[0] as File)

        const fetchConfig = {
            method: "POST",
            body: multipartFormData
        }

        getSession()
            .then(extractUserOrSignOut)
            .then(() => fetch(BACKEND_URL, fetchConfig))
            .then(async response => {
                if (!response.ok) throw await response.json()
            })
            .then(() => onUploadSuccess("Image successfully uploaded."))
            .catch(error => {
                if (error.validationErrors) setValidationErrors(error.validationErrors)
            })
            .finally(() => setIsLoading(false))
    }

    // Handle file drop with react-dropzone
    const {getRootProps, getInputProps, isDragActive} = useDropzone({
        onDrop: async (acceptedFiles) => {
            // Convert acceptedFiles array into a FileList and update the form
            const fileList = new DataTransfer()
            acceptedFiles.forEach(file => fileList.items.add(file))

            setValue("image", fileList.files)
            await trigger("image")
        }
    })

    let helperText
    if (validationErrors?.image) {
        helperText = validationErrors.image
    } else if (errors.image) {
        helperText = errors.image.message
    }

    return (
        <div className="flex flex-col items-center w-full mb-4">
            <div
                {...getRootProps()}
                className="border-dashed border-2 border-gray-500 w-full p-4 flex flex-col items-center justify-center text-center"
            >
                <input {...getInputProps()} />
                {isDragActive
                    ? <p className="mb-4">Drop the file here ...</p>
                    : <p className="mb-4">Drag and drop a file here, or click to select file</p>}

                {profileImage
                    ? <Image
                        src={profileImage}
                        className="w-32 h-32 rounded-full mb-4"
                        width={128}
                        height={128}
                        priority={true}
                        alt="Profile Image"/>
                    : <div
                        className="w-32 h-32 mb-4 rounded-full bg-gray-200 flex items-center justify-center text-gray-500">
                        No Image
                    </div>}
            </div>

            <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
                <FormProvider {...methods}>
                    <form onSubmit={handleSubmit(onSubmit)}>
                        <FileInput
                            id="image"
                            data-testid="image-update-file-input"
                            {...register("image")}
                            sizing="md"
                            helperText={helperText && (
                                <span className="font-medium text-left block">{helperText}</span>)}
                        />
                        <div className="flex justify-center w-full mt-4">
                            <div className="flex items-center gap-2">
                                <ButtonWithSpinner
                                    disabled={!formState.isValid}
                                    buttonText="Save"
                                    isLoading={isLoading}
                                    className="w-24"
                                />
                                <Button onClick={onCancel}>Cancel</Button>
                            </div>
                        </div>
                    </form>
                </FormProvider>
            </Card>
        </div>
    )
}

export default UpdateProfileImageForm