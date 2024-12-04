"use client"

import React, {useEffect, useState} from "react"
import {Card, Spinner} from "flowbite-react"
import {HiPencil} from "react-icons/hi"
import Image from "next/image"
import UpdateNameForm from "@/app/user/dashboard/components/UpdateNameForm"
import UpdateEmailAddressForm from "@/app/user/dashboard/components/UpdateEmailAddressForm"
import UpdateProfileImageForm from "@/app/user/dashboard/components/UpdateProfileImageForm"
import {extractUserOrSignOut} from "@/auth/tokenUtils"
import {getSession} from "next-auth/react"
import {FaPencilAlt} from "react-icons/fa"

const ProfileCard = ({nonVicxUserInfo}: {
    nonVicxUserInfo?: {
        username: string
        name: string
        email: string
        image?: string
    }
}) => {
    const [editingField, setEditingField] = useState<string | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [userInfo, setUserInfo] = useState(nonVicxUserInfo)
    const [updateResult, setUpdateResult] = useState("")
    const [isImageDropdownOpen, setIsImageDropdownOpen] = useState(false)

    const isVicxUser = !nonVicxUserInfo

    const handleOnCancel = () => setEditingField(null)

    const handleOnUpdateSuccess = (message: string) => {
        setUpdateResult(message)
        setEditingField(null)
        loadUserFromBackend()
    }

    const handleEditToggle = (field: string) => {
        setUpdateResult("")
        setEditingField(editingField === field ? null : field)
    }

    const toggleImageDropdown = () => setIsImageDropdownOpen((prev) => !prev)

    const deleteCurrentProfileImage = () => {
        toggleImageDropdown()
        setIsLoading(true)
        getSession()
            .then(extractUserOrSignOut)
            .then(() => fetch("/api/user/image", {method: "DELETE"}))
            .then((response) => {
                if (!response.ok) throw Error(response.statusText)
            })
            .then(() => {
                setUpdateResult("Image successfully deleted")
                loadUserFromBackend()
            })
            .finally(() => setIsLoading(false))
    }

    const loadUserFromBackend = () => {
        fetch("/api/user")
            .then((response) => {
                if (!response.ok) throw Error(response.statusText)
                return response.json()
            })
            .then((data) => {
                setUserInfo({
                    username: data.username,
                    name: data.name,
                    email: data.email,
                    image: data.hasImage ? `/api/user/image?timestamp=${Date.now()}` : undefined
                })
            })
            .finally(() => setIsLoading(false))
    }

    useEffect(() => {
        if (isVicxUser) {
            loadUserFromBackend()
        }
    }, [isVicxUser])

    if (isLoading) return <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
        <div className="flex flex-col items-center">
            <Spinner aria-label="Loading user info" size="xl" />
        </div>
    </Card>

    if (!userInfo) return <></>

    return (
        <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
            <div className="flex flex-col items-center">
                {editingField === "image"
                    ? <UpdateProfileImageForm
                        onUploadSuccess={handleOnUpdateSuccess}
                        onCancel={handleOnCancel}
                    />
                    : <div className="relative flex items-center justify-center mb-4">
                        {userInfo.image
                            ? <Image
                                alt={`${userInfo.username} image`}
                                className="w-32 h-32 rounded-full"
                                width={64}
                                height={64}
                                src={userInfo.image}
                            />
                            : <div
                                className="w-32 h-32 rounded-full bg-gray-200 flex items-center justify-center text-gray-500">
                                No Image
                            </div>}
                        <div className="absolute top-0 right-0">
                            <button
                                onClick={toggleImageDropdown}
                                className="bg-gray-200 text-gray-500 p-2 rounded-full hover:bg-gray-300 shadow-md"
                            >
                                <FaPencilAlt/>
                            </button>

                            {isImageDropdownOpen && (
                                <div
                                    className="absolute right-0 mt-2 w-48 bg-white shadow-lg rounded-lg border">
                                    <button
                                        onClick={() => {
                                            toggleImageDropdown()
                                            handleEditToggle("image")
                                        }}
                                        className="w-full text-left px-4 py-2 text-gray-700 hover:bg-gray-100"
                                    >Upload new image
                                    </button>

                                    {userInfo.image && (
                                        <button
                                            onClick={deleteCurrentProfileImage}
                                            className="w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100"
                                        >Delete image
                                        </button>)}
                                </div>)}
                        </div>
                    </div>}

                <h5 className="mb-1 text-xl font-medium text-gray-900 dark:text-white">
                    {userInfo.username}
                </h5>

                {/* Name Field */}
                <div className="flex items-center gap-2">
                    {editingField === "name"
                        ? <UpdateNameForm
                            currentName={userInfo.name}
                            onUpdateSuccess={handleOnUpdateSuccess}
                            onCancel={handleOnCancel}
                        />
                        : <>
                            <span className="text-sm text-gray-500 dark:text-gray-400">{userInfo.name}</span>
                            {isVicxUser && <HiPencil
                                className="text-gray-500 cursor-pointer hover:text-blue-500"
                                onClick={() => handleEditToggle("name")}
                                title="Edit name"
                                size={20}
                            />}
                        </>}
                </div>

                {/* Email Field */}
                <div className="flex items-center gap-2 mt-2">
                    {editingField === "email"
                        ? <UpdateEmailAddressForm
                            currentEmailAddress={userInfo.email}
                            onUpdateSuccess={handleOnUpdateSuccess}
                            onCancel={handleOnCancel}
                        />
                        : <>
                            <span className="text-sm text-gray-500 dark:text-gray-400">{userInfo.email}</span>
                            {isVicxUser && <HiPencil
                                className="text-gray-500 cursor-pointer hover:text-blue-500"
                                onClick={() => handleEditToggle("email")}
                                title="Edit email"
                                size={20}
                            />}
                        </>}
                </div>
            </div>

            {updateResult &&
                <h2 className="text-xl font-semibold text-gray-800 text-center flex items-center justify-center gap-2">
                    <span className="text-green-500">✓</span>
                    {updateResult}
                </h2>}

        </Card>
    )
}

export default ProfileCard
