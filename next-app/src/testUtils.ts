import {MockResponseInit} from "jest-fetch-mock"
import {act, fireEvent, screen} from "@testing-library/react"

export const changeEvent = (content: string) => ({target: {value: content}})

export const changeInputValue = async (label: string, value: string) => {
    const input = screen.getByLabelText(label)
    await act(() => fireEvent.change(input, changeEvent(value)))
}

export const changeInputValueByInput = async (input: HTMLElement, value: string) =>
    await act(() => fireEvent.change(input, changeEvent(value)))

export const delayedResponse = (body: string, delayInMillis: number, status: number = 200) =>
    new Promise<MockResponseInit>(resolve =>
        setTimeout(
            () => resolve({body, status}),
            delayInMillis)
    )
