"use client"

import Image from "next/image"
import {useEffect, useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import * as yup from "yup"
import {InferType} from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import {FileInput, Label} from "flowbite-react"

import fallbackProfileImage from "@/assets/images/profile.png"
import {signIn} from "next-auth/react"
import {signInOptions} from "@/components/navbar/navbarConstants"
import {DEFAULT_APP_PROVIDER_ID} from "@/constants/authProviders"

// put this in next-app/.env.local
// NEXT_PUBLIC_USER_BACKEND_URL=http://localhost:8080/api/user
const BACKEND_URL = process.env.NEXT_PUBLIC_USER_BACKEND_URL || "/backend-spring-boot/api/user"

const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/

const UserSignupSchema = yup.object({
    username: yup
        .string()
        .required("Username is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`),

    name: yup
        .string()
        .required("Name is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`),

    email: yup
        .string()
        .required("Email is required")
        .email("Please enter a valid email address"),

    password: yup
        .string()
        .required("Password is required")
        .min(8, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`)
        .matches(
            passwordRegex,
            "Password must have at least one uppercase, one lowercase letter, and one number"
        ),

    repeat_password: yup
        .string()
        .required("Please confirm password")
        .oneOf([yup.ref('password')], "Passwords must match"),

    image: yup.mixed<FileList>()
        .test(
            "file-type",
            "Only PNG and JPG files are allowed",
            (files: FileList | undefined) => {
                const file = files?.[0]
                return file
                    ? ["image/png", "image/jpeg"].includes(file.type)
                    : true
            })
        .test(
            "file-size",
            "File size exceeds the maximum allowed size of 51200 bytes",
            (files: FileList | undefined) => {
                const file = files?.[0]
                return file
                    ? file.size < 51_201
                    : true
            }
        )

})

type UserSignupFormData = InferType<typeof UserSignupSchema>

const UserSignupForm = () => {
    const [profileImage, setProfileImage] = useState<string | undefined>(undefined)
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [result, setResult] = useState<string>()
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>()

    const methods = useForm<UserSignupFormData>({
        resolver: yupResolver(UserSignupSchema),
        mode: "onChange"
    })

    const {
        handleSubmit,
        formState,
        formState: {
            errors,
        },
        register,
        watch
    } = methods

    const watchedFile = watch("image")
    const watchedUsername = watch("username")

    useEffect(() => {
        if (errors.image) return

        if (watchedFile?.[0]) {
            const file = watchedFile[0]
            const reader = new FileReader()
            reader.onload = () => {
                setProfileImage(reader.result as string)
            }
            reader.readAsDataURL(file)
        }
    }, [watchedFile, errors.image])

    useEffect(() => {
        setValidationErrors(undefined)
    }, [watchedUsername])

    const onSubmit = async (formData: UserSignupFormData) => {
        const multipartFormData = new FormData()
        multipartFormData.append("username", formData.username)
        multipartFormData.append("name", formData.name)
        multipartFormData.append("email", formData.email)
        multipartFormData.append("password", formData.password)
        if (formData.image?.[0]) {
            multipartFormData.append("image", formData.image?.[0])
        }

        setValidationErrors(undefined)
        setIsLoading(true)

        const fetchConfig = {
            method: "POST",
            body: multipartFormData
        }

        fetch(BACKEND_URL, fetchConfig)
            .then(async response => {
                if (!response.ok) {
                    // Parse error response as JSON to extract validation errors
                    throw await response.json()
                }
                return response.text()
            })
            .then(data => setResult(data))
            .catch(error => {
                if (error.validationErrors) {
                    setValidationErrors(error.validationErrors)
                }
            })
            .finally(() => setIsLoading(false))
    }

    if (result) {
        return <div className="mt-6 p-6 bg-gray-50 border rounded-lg shadow-sm">
            <h2 className="text-xl font-semibold text-gray-800 text-center flex items-center justify-center gap-2">
                <span className="text-green-500">✓</span>
                {result}
            </h2>
            <p className="mt-4 text-center text-gray-600">
                To access your account, please log in by clicking the button below.
            </p>
            <div className="mt-4 text-center">
                <button
                    onClick={async () => await signIn(DEFAULT_APP_PROVIDER_ID, signInOptions)}
                    className="px-4 py-2 bg-green-500 text-white font-semibold rounded-lg shadow hover:bg-green-600"
                >
                    Log In
                </button>
            </div>
        </div>
    }

    return (
        <div className="flex flex-col items-center m-4">
            <Image
                src={profileImage || fallbackProfileImage}
                className="w-32 h-32 rounded-full"
                width={64}
                height={64}
                alt="Profile Image"
            />

            <div className="flex justify-center w-full mt-4">
                <FormProvider {...methods}>
                    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4 w-full max-w-lg">
                        <ValidatedTextInput
                            label="Username"
                            errorMessage={validationErrors?.username}
                            name="username"/>
                        <ValidatedTextInput label="Name" name="name"/>
                        <ValidatedTextInput label="Email" name="email"/>
                        <ValidatedTextInput label="Password" name="password" type="password"/>
                        <ValidatedTextInput label="Repeat Password" name="repeat_password" type="password"/>

                        <div className="flex flex-col">
                            <Label
                                htmlFor="image"
                                className={`mb-1 text-left ${
                                    errors.image ? "text-red-500" : "text-gray-700"
                                }`}
                                color={errors.image ? "failure" : "success"}
                                value="Profile Image"
                            />

                            <FileInput
                                id="image"
                                {...register("image")}
                                //onChange={handleFileChange}
                                sizing="md"
                                helperText={
                                    errors.image && (
                                        <span className="font-medium text-left block">
                                        {(errors.image as { message?: string }).message}
                                </span>)
                                }
                            />
                        </div>

                        <ButtonWithSpinner
                            disabled={!formState.isValid}
                            buttonText="Submit"
                            isLoading={isLoading}
                            className="col-span-1 mt-4 w-full"
                        />
                    </form>
                </FormProvider>
            </div>
            {/*
            {result && (
                <div className="m-4">
                    {isErrorResponse(result) ? (
                        <div
                            className="text-red-600 font-bold text-xl bg-red-100 border border-red-400 rounded p-4 mx-4">
                            {result.error}
                        </div>
                    ) : (
                        Object.entries((result as KMeansSuccessResponse)).map(([score, grade], index) =>
                            <div
                                key={index}
                                className={`mt-2 p-2 rounded text-white ${gradeToColorMap[(grade as Grade)]}`}
                            >Score: {score}, Grade: {grade}</div>
                        )
                    )}
                </div>
            )}
*/}
        </div>
    )
}

export default UserSignupForm