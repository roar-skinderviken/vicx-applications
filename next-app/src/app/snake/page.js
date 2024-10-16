import Script from "next/script"
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome"
import {faArrowDown, faArrowLeft, faArrowRight, faArrowUp} from "@fortawesome/free-solid-svg-icons"

export const dynamic = "force-static"

export default function SnakePage() {
    return (
        <div className="content">
            <div id="game-container" className="container">
                {/*Score Display*/}
                <div id="score-display">Score: 0</div>
                {/*Game Board*/}
                <div id="game-board"></div>
                {/*Control Buttons*/}
                <div id="control-buttons">
                    <div></div>
                    <button className="btn" id="up">
                        <FontAwesomeIcon icon={faArrowUp} className="fa-fw"/>
                    </button>
                    <div></div>
                    <button className="btn" id="left">
                        <FontAwesomeIcon icon={faArrowLeft} className="fa-fw"/>
                    </button>
                    <div className="empty-space"></div>
                    <button className="btn" id="right">
                        <FontAwesomeIcon icon={faArrowRight} className="fa-fw"/>
                    </button>
                    <div></div>
                    <button className="btn" id="down">
                        <FontAwesomeIcon icon={faArrowDown} className="fa-fw"/>
                    </button>
                </div>
                {/*Restart Button*/}
                <button id="restart-button" className="btn btn-primary">Restart Game</button>
            </div>

            <Script id="snake-game" strategy="afterInteractive">
                {`
                    const GRID_SIZE = 20
                    const CELL_SIZE = 20
                    const INITIAL_SPEED = 200
    
                    let snake = [{x: 10, y: 10}]
                    let food = {x: 15, y: 10}
                    let dx = 1
                    let dy = 0
                    let score = 0
                    let speed = INITIAL_SPEED
                    let gameInterval
    
                    const gameBoard = document.getElementById('game-board')
                    const scoreDisplay = document.getElementById('score-display')
                    const restartButton = document.getElementById('restart-button')
    
                    function startGame() {
                        snake = [{x: 10, y: 10}]
                        food = {x: 15, y: 10}
                        dx = 1
                        dy = 0
                        score = 0
                        speed = INITIAL_SPEED
                        gameBoard.innerHTML = ''
                        updateScore()
                        clearInterval(gameInterval)
                        gameInterval = setInterval(updateGame, speed)
                        document.addEventListener('keydown', changeDirection)
                        document.getElementById('left').addEventListener('click', () => changeDirection({key: 'ArrowLeft'}))
                        document.getElementById('up').addEventListener('click', () => changeDirection({key: 'ArrowUp'}))
                        document.getElementById('down').addEventListener('click', () => changeDirection({key: 'ArrowDown'}))
                        document.getElementById('right').addEventListener('click', () => changeDirection({key: 'ArrowRight'}))
                        drawSnake()
                        drawFood()
                        restartButton.style.display = 'none'
                    }
    
                    function drawSnake() {
                        snake.forEach(segment => {
                            const snakeElement = document.createElement('div')
                            snakeElement.style.gridRowStart = segment.y
                            snakeElement.style.gridColumnStart = segment.x
                            snakeElement.classList.add('snake')
                            gameBoard.appendChild(snakeElement)
                        })
                    }
    
                    function drawFood() {
                        const foodElement = document.createElement('div')
                        foodElement.style.gridRowStart = food.y
                        foodElement.style.gridColumnStart = food.x
                        foodElement.classList.add('food')
                        gameBoard.appendChild(foodElement)
                    }
    
                    function updateGame() {
                        const head = {x: snake[0].x + dx, y: snake[0].y + dy}
                        snake.unshift(head)
        
                        if (head.x === food.x && head.y === food.y) {
                            score++
                            updateScore()
                            generateFood()
                        } else {
                            snake.pop()
                        }
        
                        if (isCollision()) {
                            endGame()
                            return
                        }
    
                        gameBoard.innerHTML = ''
                        drawSnake()
                        drawFood()
                    }
    
                    function generateFood() {
                        food.x = Math.floor(Math.random() * GRID_SIZE) + 1
                        food.y = Math.floor(Math.random() * GRID_SIZE) + 1
                        while (snake.some(segment => segment.x === food.x && segment.y === food.y)) {
                            food.x = Math.floor(Math.random() * GRID_SIZE) + 1
                            food.y = Math.floor(Math.random() * GRID_SIZE) + 1
                        }
                    }
    
                    function isCollision() {
                        const head = snake[0]
                        return (
                            head.x < 1 || head.x > GRID_SIZE ||
                            head.y < 1 || head.y > GRID_SIZE ||
                            snake.slice(1).some(segment => segment.x === head.x && segment.y === head.y)
                        )
                    }
    
                    function changeDirection(event) {
                        switch (event.key) {
                            case 'ArrowUp':
                                if (dy === 0) {
                                    dx = 0
                                    dy = -1
                                }
                                break
    
                            case 'ArrowDown':
                                if (dy === 0) {
                                    dx = 0
                                    dy = 1
                                }
                                break
    
                            case 'ArrowLeft':
                                if (dx === 0) {
                                    dx = -1
                                    dy = 0
                                }
                                break
                    
                            case 'ArrowRight':
                                if (dx === 0) {
                                    dx = 1
                                    dy = 0
                                }
                                break
                        }
                    }
    
                    function endGame() {
                        clearInterval(gameInterval)
                        document.removeEventListener('keydown', changeDirection)
                        restartButton.style.display = 'block'
                    }
    
                    function updateScore() {
                        scoreDisplay.textContent = "Score: " + score
                    }

                    restartButton.addEventListener('click', startGame)

                    startGame()
                `}
            </Script>
        </div>
    )
}