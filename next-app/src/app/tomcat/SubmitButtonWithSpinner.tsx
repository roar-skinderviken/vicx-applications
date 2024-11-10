import {Button, Spinner} from "flowbite-react"

const SubmitButtonWithSpinner = (
    {
        buttonText,
        disabled,
        isLoading,
        onButtonClick = () => {
        }
    }: {
        buttonText: string
        disabled: boolean
        isLoading: boolean
        onButtonClick: () => void
    }) => <Button
    type="submit"
    disabled={disabled}
    onClick={onButtonClick}>
    {isLoading
        ? <>
            <Spinner aria-label="Loading addition result" size="sm"/>
            <span className="pl-3">Loading...</span>
        </>
        : <>{buttonText}</>
    }
</Button>

export default SubmitButtonWithSpinner