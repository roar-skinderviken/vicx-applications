"use client"

import {Button, Checkbox, Label, Table} from "flowbite-react"
import {CalculationResult} from "@/app/tomcat/CalculatorFormAndResult"
import {ChangeEvent, useEffect, useState} from "react"

export const formatDate = (date: Date) =>
    new Intl.DateTimeFormat('en-US', {
        dateStyle: 'medium',
        timeStyle: 'short'
    }).format(new Date(date))

export const formatCalculation = (calculation: CalculationResult) => {
    const operation = calculation.operation === "PLUS" ? "+" : "-"

    return `${calculation.firstValue} ${operation} ${calculation.secondValue} = ${calculation.result}`
}

const PreviousCalculations = (
    {
        calculations,
        username,
        onDelete = () => {
        }
    }: {
        calculations: CalculationResult[],
        username?: string,
        onDelete?: (idsToDelete: number[]) => void
    }) => {
    const [selectedItems, setSelectedItems] = useState<number[]>([])

    useEffect(()=> setSelectedItems([]),[calculations])

    const isAllSelected = calculations
        .filter(it => it.username === username)
        .every(it => selectedItems.includes(it.id))

    const handleCheckboxChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {checked, value} = event.target
        const numericValue = Number(value)

        setSelectedItems(prev =>
            checked
                ? [...prev, numericValue]
                : prev.filter(it => it !== numericValue)
        )
    }

    const handleSelectAllChange = (event: ChangeEvent<HTMLInputElement>) =>
        setSelectedItems(event.target.checked
            ? calculations
                .filter(it => it.username === username)
                .map(it => it.id)
            : []
        )

    if (calculations.length < 1) return <></>

    return (
        <div className="mt-8">
            <div className="relative flex items-center justify-center mb-3">
                {username && (
                    <Button
                        className="absolute left-4"
                        size="sm"
                        disabled={!selectedItems.length}
                        onClick={() => onDelete(selectedItems)}
                    >Delete selected</Button>
                )}
                <h3 className="text-md font-semibold text-gray-700">
                    Previous results on this server
                </h3>
            </div>

            <div className="overflow-x-auto">
                <Table hoverable>
                    <Table.Head>
                        {username && (
                            <Table.HeadCell className="p-4 flex items-center space-x-2">
                                {calculations.some(it => it.username === username) && (
                                    <>
                                        <Checkbox
                                            id="selectAll"
                                            checked={isAllSelected}
                                            value={1}
                                            data-testid="select-all-checkbox"
                                            onChange={handleSelectAllChange}
                                        />
                                        <Label
                                            htmlFor="selectAll"
                                            className="text-sm text-gray-500 dark:text-gray-400"
                                        >Select all</Label>
                                    </>
                                )}
                            </Table.HeadCell>
                        )}
                        <Table.HeadCell>Calculation</Table.HeadCell>
                        <Table.HeadCell>Username</Table.HeadCell>
                        <Table.HeadCell>Date</Table.HeadCell>
                    </Table.Head>
                    <Table.Body className="divide-y">
                        {calculations.map((currentCalculation, index) => (
                            <Table.Row
                                key={index}
                                className="bg-white dark:border-gray-700 dark:bg-gray-800">
                                {username && (
                                    <Table.Cell className="p-4">
                                        {username === currentCalculation.username &&
                                            <Checkbox
                                                value={currentCalculation.id}
                                                checked={selectedItems.some(it => it === currentCalculation.id)}
                                                onChange={handleCheckboxChange}
                                                data-testid={`checkbox-${currentCalculation.id}`}
                                            />
                                        }
                                    </Table.Cell>
                                )}
                                <Table.Cell className="whitespace-nowrap font-medium text-gray-900 dark:text-white">
                                    {formatCalculation(currentCalculation)}
                                </Table.Cell>
                                <Table.Cell>{currentCalculation.username || "Anonymous"}</Table.Cell>
                                <Table.Cell>{formatDate(currentCalculation.createdAt)}</Table.Cell>
                            </Table.Row>
                        ))}
                    </Table.Body>
                </Table>
            </div>
        </div>
    )
}

export default PreviousCalculations