import * as yup from "yup"

const USERNAME_REGEXT = /^[a-zA-Z0-9_-]+$/
const PASSWORD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/

export const UserRegistrationSchema = yup.object({
    username: yup
        .string()
        .trim()
        .required("Username is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`)
        .matches(
            USERNAME_REGEXT,
            "Username can only contain letters, numbers, hyphens, and underscores"
        ),
    name: yup
        .string()
        .trim()
        .required("Name is required")
        .min(4, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`),

    email: yup
        .string()
        .trim()
        .required("Email is required")
        .email("Please enter a valid email address"),

    password: yup
        .string()
        .required("Password is required")
        .min(8, ({min}) => `It must have a minimum of ${min} characters`)
        .max(255, ({max}) => `It must have a maximum of ${max} characters`)
        .matches(
            PASSWORD_REGEX,
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
        ),

    reCaptchaToken: yup
        .string()
        .required("Please verify that you're not a robot")
})
