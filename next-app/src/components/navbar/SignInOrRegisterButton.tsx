import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import {IconDefinition} from "@fortawesome/fontawesome-svg-core"

const SignInOrRegisterButton = ({icon, buttonText, onClick}: {
    icon: IconDefinition
    buttonText: string
    onClick: () => void
}) => {
    return <button
        onClick={onClick}
        className="text-gray-400 hover:bg-gray-50 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white rounded-md md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white flex items-center justify-center"
    >
        {/* mobile devices */}
        <span className="block md:hidden">
            <FontAwesomeIcon icon={icon} className="mt-1.5 text-gray-400 text-[22px]"/>
        </span>
        {/* big-screen devices */}
        <span className="hidden md:block whitespace-no-wrap">{buttonText}</span>
    </button>
}

export default SignInOrRegisterButton