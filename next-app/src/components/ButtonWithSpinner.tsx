import {Button, Spinner} from "flowbite-react"
import {ButtonHTMLAttributes} from "react"

const ButtonWithSpinner = (
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
            ? <div className="flex items-center justify-center gap-2 px-4">
                <Spinner
                    aria-label="Loading addition result"
                    size="sm"/>
                <span>Loading...</span>
            </div>
            : <>{buttonText}</>
        }
    </Button>
)

export default ButtonWithSpinner