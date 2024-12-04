import * as yup from "yup"

export const MAX_IMAGE_FILE_SIZE = 51_200 // 50 KB
export const SUPPORTED_IMAGE_FORMATS = ["image/png", "image/jpeg"]
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/

export const passwordSchema = yup.object({
    password: yup
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
        .required("Please confirm password")
        .oneOf([yup.ref('password')], "Passwords must match")
})

export const emailAddressSchema = yup.object({
    email: yup
        .string()
        .trim()
        .required("Email is required")
        .email("Please enter a valid email address")
})

export const nameSchema = yup.object({
    name: yup
        .string()
        .trim()
        .required("Name is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`),
})