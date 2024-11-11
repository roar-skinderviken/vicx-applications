import {Button, Spinner} from "flowbite-react"
import {ButtonHTMLAttributes} from "react"

const SubmitButtonWithSpinner = (
    {
        buttonText,
        disabled,
        isLoading,
        onButtonClick = () => {
        },
        ...rest
    }: {
        buttonText: string
        disabled: boolean
        isLoading: boolean
        onButtonClick?: () => void
    } & ButtonHTMLAttributes<HTMLButtonElement>) => (
    <Button
        type="submit"
        disabled={disabled}
        onClick={onButtonClick}
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