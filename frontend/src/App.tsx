import {useState} from 'react';
import './App.css';

const baseUrl = import.meta.env.BASE_URL;

const App = () => {
    const [firstValue, setFirstValue] = useState<number>(0);
    const [secondValue, setSecondValue] = useState<number>(0);
    const [operation, setOperation] = useState<string>('');
    const [result, setResult] = useState<number | null>(null);

    const handleCalculation = async (operation: 'plus' | 'minus') => {
        setOperation(operation);
        const response = await fetch(`${baseUrl}api/${firstValue}/${secondValue}/${operation}`);
        const data = await response.json();
        setResult(data.result);
    };

    return <div>
        <div className="mb-3">
            <label htmlFor="firstValue" className="form-label">First Value Hello World</label>
            <input
                type="number"
                className="form-control"
                id="firstValue"
                value={firstValue}
                onChange={(e) => setFirstValue(parseInt(e.target.value))}
            />
        </div>
        <div className="mb-3">
            <label htmlFor="secondValue" className="form-label">Second Value</label>
            <input
                type="number"
                className="form-control"
                id="secondValue"
                value={secondValue}
                onChange={(e) => setSecondValue(parseInt(e.target.value))}
            />
        </div>
        <div>
            <button
                className="btn btn-primary me-2"
                onClick={() => handleCalculation('plus')}>
                Add
            </button>
            <button
                className="btn btn-secondary"
                onClick={() => handleCalculation('minus')}>
                Subtract
            </button>
        </div>

        {result !== null && (
            <div className="mt-3">
                <h4>Result</h4>
                <p>
                    {firstValue} {operation === 'plus' ? '+' : '-'} {secondValue} = {result}
                </p>
            </div>
        )}
    </div>
};

export default App;