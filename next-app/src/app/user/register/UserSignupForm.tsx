"use client"

import Image from "next/image"
import React, {useEffect, useState} from "react"
import {FormProvider, useForm} from "react-hook-form"
import {yupResolver} from "@hookform/resolvers/yup"
import {InferType} from "yup"
import ValidatedTextInput from "@/components/ValidatedTextInput"
import ButtonWithSpinner from "@/components/ButtonWithSpinner"
import {FileInput, Label} from "flowbite-react"

// @ts-expect-error by some reason
import ReCAPTCHA from "react-google-recaptcha"

import fallbackProfileImage from "@/assets/images/profile.png"
import {signIn} from "next-auth/react"
import {signInOptions} from "@/components/navbar/navbarConstants"
import {DEFAULT_OAUTH_CLIENT_ID} from "@/auth/authProviders"
import {userRegistrationSchema} from "@/app/user/register/userRegistrationSchema"

// put this in next-app/.env.local
// NEXT_PUBLIC_USER_BACKEND_URL=http://localhost:8080/api/user
const BACKEND_URL = process.env.NEXT_PUBLIC_USER_BACKEND_URL || "/backend-spring-boot/api/user"

type UserSignupFormData = InferType<typeof userRegistrationSchema>

const UserSignupForm = ({reCaptchaSiteKey}: { reCaptchaSiteKey: string }) => {
    const [profileImage, setProfileImage] = useState<string | undefined>(undefined)
    const [isLoading, setIsLoading] = useState<boolean>(false)
    const [result, setResult] = useState<string>()
    const [validationErrors, setValidationErrors] = useState<Record<string, string>>()

    const methods = useForm<UserSignupFormData>({
        resolver: yupResolver(userRegistrationSchema),
        mode: "onChange"
    })

    const {
        handleSubmit,
        formState,
        formState: {
            errors
        },
        register,
        watch,
        trigger
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
        multipartFormData.append("recaptchaToken", formData.reCaptchaToken)

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
                    onClick={async () => await signIn(DEFAULT_OAUTH_CLIENT_ID, signInOptions)}
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
                        <input type="hidden" {...register("reCaptchaToken")} />

                        <ValidatedTextInput
                            label="Username"
                            errorMessage={validationErrors?.username}
                            name="username"/>
                        <ValidatedTextInput label="Name" name="name"/>
                        <ValidatedTextInput label="Email" name="email"/>
                        <ValidatedTextInput label="Password" name="password" type="password"/>
                        <ValidatedTextInput label="Repeat Password" name="confirmPassword" type="password"/>

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
                                sizing="md"
                                helperText={
                                    errors.image && (
                                        <span className="font-medium text-left block">
                                        {(errors.image as { message?: string }).message}
                                </span>)
                                }
                            />
                        </div>

                        <div className="flex flex-col items-center">
                            <ReCAPTCHA
                                sitekey={reCaptchaSiteKey}
                                onChange={async (token: string) => {
                                    setValidationErrors(undefined)
                                    methods.setValue("reCaptchaToken", token)
                                    await trigger('reCaptchaToken')
                                }}
                            />

                            {validationErrors?.recaptchaToken && (
                                <div style={{color: 'red', marginTop: '10px'}}>
                                    {validationErrors.recaptchaToken}
                                </div>
                            )}
                        </div>

                        <ButtonWithSpinner
                            disabled={!formState.isValid}
                            buttonText="Submit"
                            isLoading={isLoading}
                            className="col-span-1 mt-2 w-full"
                        />
                    </form>
                </FormProvider>
            </div>
        </div>
    )
}

export default UserSignupForm