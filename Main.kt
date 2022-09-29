class Game(val sizeField : Int)
{
    private val totalFields = sizeField * sizeField
    private val field = Array(totalFields) { IntArray(totalFields) { -1 } }  // Значения: -1 = пустое поле; 0 = нолик; 1 = крестик

    var lastX : Int = 0  // Координаты прошлого хода -> сейчас это координаты текущего активного поля
    var lastY : Int = 0

    var whoMove : Boolean = false  // Если false (0), то ходит 1-ый игрок за нолики, иначе 2-ой игрок за крестики
    var isEndGame : Boolean = false

    fun doMove(y : Int, x : Int)
    {
        if (field[lastY * sizeField + lastX][y * sizeField + x] != -1)
        {
            println("В этой клетке уже есть метка! (${if (field[lastY * sizeField + lastX][y * sizeField + x] == 0) "нолик" else "крестик"})")
            return
        }

        field[lastY * sizeField + lastX][y * sizeField + x] = if (whoMove) 1 else 0

        checkWinning(lastY * sizeField + lastX)

        if (isEndGame)
            return

        lastX = x
        lastY = y

        whoMove = !whoMove
    }

    fun printSubField(numberField : Int)
    {
        print("|" + "=".repeat(sizeField*2-1) + "|")

        for (i in 0 until sizeField)
        {
            print("\n|")
            for (j in 0 until sizeField)
            {
                when (field[numberField][i * sizeField + j])
                {
                    0 -> print("o")
                    1 -> print("x")
                    -1 -> print(" ")
                }
                print("|")
            }
        }

        println("\n|" + "=".repeat(sizeField*2-1) + "|")
    }

    fun printField()
    {
        println("| " + ("=".repeat(sizeField*2+1) + " | ").repeat(sizeField))

        for (startBlock in 0 until totalFields step sizeField)
        {
            for (numRow in 0 until sizeField)
            {
                for (it_block in 0 until sizeField)
                {
                    print("| |")
                    for (col in 0 until sizeField)
                    {
                        when (field[startBlock + it_block][numRow * sizeField + col]) {
                            0 -> print("o")
                            1 -> print("x")
                            -1 -> print(" ")
                        }
                        print("|")
                    }
                    print(" ")
                }
                if (numRow != sizeField - 1)
                print("|\n| " + ("-".repeat(sizeField*2+1) + " | ").repeat(sizeField) + "\n")
            }
            print("|\n| " + ("=".repeat(sizeField*2+1) + " | ").repeat(sizeField) + "\n")
        }
    }

    fun printNowField()
    {
        printSubField(lastY * sizeField + lastX)
    }

    // Проверяет был ли ход победным и влияет на флаг isEndGame
    private fun checkWinning(numberField: Int)
    {
        val nowField = field[numberField]

        for (i in 0 until sizeField)    // Проверка строк и столбцов
        {
            val row = nowField.slice(i*sizeField until i*sizeField + sizeField).distinct()
            val col = nowField.slice(i until totalFields step sizeField).distinct()
            if ((row.size == 1 && row[0] != -1) || (col.size == 1 && col[0] != -1))
            {
                isEndGame = true
                return
            }
        }

        // Проверка диагоналей
        val leftDiagonal = nowField.slice(0 until totalFields step sizeField+1).distinct()
        val rightDiagonal = nowField.slice(sizeField until totalFields step sizeField-1).distinct()
        if ((leftDiagonal.size == 1 && leftDiagonal[0] != -1) || (rightDiagonal.size == 1 && rightDiagonal[0] != -1))
            {
                isEndGame = true
                return
            }
    }
}

class InputHandler(private val gameObj : Game)
{
    var exit = false
    var error = false

    fun handler(command : String)
    {
        try
        {
            error = false
            if (command == "p" || command == "print")
                gameObj.printField()
            else if (command == "pnf" || command == "print now field")
                gameObj.printNowField()
            else if ((command.contains("pf") || command.contains("print field")) && command.split(' ').size == 3)
                gameObj.printSubField(command.split(' ')[1].toInt() * gameObj.sizeField + command.split(' ')[2].toInt())
            else if ((command[0] == 'm' || command.contains("move")) && command.split(' ').size == 3)
                gameObj.doMove(command.split(' ')[1].toInt() - 1, command.split(' ')[2].toInt() - 1)
            else if (command[0] == 'h' || command == "help")
                help()
            else if (command[0] == 'e' || command == "exit")
                exit = true
            else
            {
                println("Команда не опознана! Проверьте синтаксис и аргументы")
                error = true
            }
        }
        catch (e: Exception)
        {
            println("Неверный ввод! (${e.message})")
            error = true
        }
    }

    private fun help()
    {
        println("""Команды :
            p или print <---> печать матрицы полей
            pf y x или print field y x <---> печать поля с координатами y (строка) x (столбец)
            pnf или print now field <---> печать активного поля
            m или move y x <---> ход игрока на активное поле с координатами y (строка) x (столбец)
            e или exit <---> выход из игры
            """)
    }
}

fun main()
{
    val N = 2
    val game = Game(N)
    val strInputHandler = InputHandler(game)

    println("Ход ${if (game.whoMove) "крестиков" else "ноликов"} в поле (${game.lastY + 1}, ${game.lastX + 1})")

    while (!game.isEndGame && !strInputHandler.exit)
    {
        print(">> ")
        val strInput : String = readLine() ?: "m"

        strInputHandler.handler(strInput)

        if (!game.isEndGame && !strInputHandler.exit)
        {
            println("Ход ${if (game.whoMove) "крестиков" else "ноликов"} в поле (${game.lastY + 1}, ${game.lastX + 1})")
        }
    }

    if (game.isEndGame)
        println("${if (game.whoMove) "xxx" else "ooo"} Победил ${if (game.whoMove) "первый" else "второй"} игрок ${if (game.whoMove) "xxx" else "ooo"}")
    else
        println("Программа завершена!")
}