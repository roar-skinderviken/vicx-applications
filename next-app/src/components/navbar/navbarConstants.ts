export const signOutOptions = {callbackUrl: "/signed-out", redirect: true}
export const signInOptions = {callbackUrl: '/dashboard', redirect: true}

// see https://flowbite-react.com/docs/components/navbar
export const navbarTheme = {
    root: {
        base: "bg-gray-800 sticky top-0 z-50 px-2 py-3 dark:border-gray-700 dark:bg-gray-800 sm:px-4"
    },
    link: {
        active: {
            off: "border-b border-gray-100 text-gray-400 hover:bg-gray-50 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white md:border-0 md:hover:bg-transparent md:hover:text-cyan-700 md:dark:hover:bg-transparent md:dark:hover:text-white",
        },
    },
}

