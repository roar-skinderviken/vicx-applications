import * as yup from "yup"
import {
    emailAddressSchema, MAX_IMAGE_FILE_SIZE,
    nameSchema,
    passwordSchema,
    SUPPORTED_IMAGE_FORMATS
} from "@/utils/yupSharedSchemas"

const USERNAME_REGEX = /^[a-zA-Z0-9_-]+$/

export const userRegistrationSchema = yup.object({
    username: yup
        .string()
        .trim()
        .required("Username is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`)
        .matches(
            USERNAME_REGEX,
            "Username can only contain letters, numbers, hyphens, and underscores"
        ),

    name: yup.reach(nameSchema, "name") as yup.StringSchema<string>,
    email: yup.reach(emailAddressSchema, "email") as yup.StringSchema<string>,
    password: yup.reach(passwordSchema, "password") as yup.StringSchema<string>,
    confirmPassword: yup.reach(passwordSchema, "confirmPassword") as yup.StringSchema<string>,

    image: yup.mixed<FileList>()
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
        ).required(),

    reCaptchaToken: yup
        .string()
        .required("Please verify that you're not a robot")
})
