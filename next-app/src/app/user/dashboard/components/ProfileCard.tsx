"use client"

import React, {useState} from "react"
import {Card} from "flowbite-react"
import {HiPencil} from "react-icons/hi"
import Image from "next/image"
import {SessionUser} from "@/types/authTypes"

import fallbackProfileImage from "@/assets/images/profile.png"
import UpdateNameForm from "@/app/user/dashboard/components/UpdateNameForm"
import UpdateEmailAddressForm from "@/app/user/dashboard/components/UpdateEmailAddressForm"

const ProfileCard = ({sessionUser}: { sessionUser: SessionUser }) => {
    const [editingField, setEditingField] = useState<string | null>(null)

    const editEnabled = sessionUser?.roles?.includes("ROLE_USER")

    const handleEditToggle = (field: string) => {
        setEditingField(editingField === field ? null : field)
    }

    return (
        <Card className="max-w-md sm:max-w-lg md:max-w-xl lg:max-w-2xl m-2 w-full">
            <div className="flex flex-col items-center">
                <Image
                    alt={`${sessionUser.id} image`}
                    height="96"
                    src={sessionUser?.image || fallbackProfileImage.src}
                    width="96"
                    className="mb-3 rounded-full shadow-lg"
                />
                <h5 className="mb-1 text-xl font-medium text-gray-900 dark:text-white">
                    {sessionUser.id}
                </h5>

                {/* Name Field */}
                <div className="flex items-center gap-2">
                    {editingField === "name" ? (
                        <UpdateNameForm
                            currentName={sessionUser.name as string}
                            onEndEdit={() => setEditingField(null)}/>
                    ) : (
                        <>
                            <span className="text-sm text-gray-500 dark:text-gray-400">{sessionUser.name}</span>
                            {editEnabled && <HiPencil
                                className="text-gray-500 cursor-pointer hover:text-blue-500"
                                onClick={() => handleEditToggle("name")}
                                title="Edit name"
                                size={20}
                            />}
                        </>
                    )}
                </div>

                {/* Email Field */}
                <div className="flex items-center gap-2 mt-2">
                    {editingField === "email" ? (
                        <UpdateEmailAddressForm
                            currentEmailAddress={sessionUser.email as string}
                            onEndEdit={() => setEditingField(null)}/>
                    ) : (
                        <>
                            <span className="text-sm text-gray-500 dark:text-gray-400">{sessionUser.email}</span>
                            {editEnabled && <HiPencil
                                className="text-gray-500 cursor-pointer hover:text-blue-500"
                                onClick={() => handleEditToggle("email")}
                                title="Edit email"
                                size={20}
                            />}
                        </>
                    )}
                </div>
            </div>
        </Card>
    )
}

export default ProfileCard
