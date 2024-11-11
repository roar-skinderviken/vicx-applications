import {Button, Spinner} from "flowbite-react"
import {ButtonHTMLAttributes} from "react"

const SubmitButtonWithSpinner = (
    {
        buttonText,
        isLoading,
        disabled = false,
        type = "submit",
        size,
        onClick = () => {
        },
        ...rest
    }: {
        buttonText: string
        isLoading: boolean
        size?: "xs" | "sm" | "lg" | "xl"
    } & ButtonHTMLAttributes<HTMLButtonElement>) => (
    <Button
        type={type}
        size={size}
        disabled={disabled || isLoading}
        onClick={onClick}
        {...rest}>
        {isLoading
            ? <>
                <Spinner aria-label="Loading addition result" size="sm"/>
                <span className="pl-3">Loading...</span>
            </>
            : <>{buttonText}</>
        }
    </Button>
)

export default SubmitButtonWithSpinner