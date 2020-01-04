package flashcards
import java.io.File
import java.util.*


fun main(args: Array<String>) {
    val flashCards = mutableMapOf<String, String>()
    val wrongAnswers = mutableMapOf<String, Int>()

    initialImport(args, flashCards)
    val exportToFile = exitExport(args)

    while (true) {
        log()
        log("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (getInput()) {
            "add" -> addACard(flashCards)
            "remove" -> remove(flashCards)
            "import" -> {
                log("File name:")
                import(getInput(),flashCards)
            }
            "export" -> {
                log("File name:")
                export(getInput(),flashCards)
            }
            "ask" -> ask(flashCards,wrongAnswers)
            "exit" -> {
                log("Bye bye!")
                export(exportToFile, flashCards)
                return
            }
            "log" -> getLog()
            "hardest card" -> hardestCard(wrongAnswers)
            "reset stats" -> wrongAnswers.clear()
        }
    }
}

fun getLog(){
    val file = File("/Users/jay/Desktop/log.txt")
    file.forEachLine { println(it) }
}

fun initialImport(args: Array<String>, flashCards: MutableMap<String, String>): String {
    log("Started flashcards .........")
    if (args.contains("-import")) {
        for (i in args.indices) {
            if(args[i] == "-import") {
                import(args[i+1], flashCards)
                return args[i+1]
            }
        }
    }
    return ""
}

fun exitExport(args: Array<String>): String {
    if (args.contains("-export")) {
        for (i in args.indices) {
            if(args[i] == "-export") {
                return args[i+1]
            }
        }
    }
    return ""
}

fun log(string: String = "", echo: Boolean = true){
    if (echo) {println(string)}
    val str = string + "\n"
    val file = File("/Users/jay/Desktop/log.txt")
    if (file.exists()) {
        file.appendText(str)
    } else {
        file.writeText(str)
    }
}

fun hardestCard(wrongAnswers: MutableMap<String, Int>){
    val maxErrors = wrongAnswers.maxBy { it.value }
    if (maxErrors == null) {
        log("There are no cards with errors.")
    }else {
            log("The hardest card is \"${maxErrors.key}\". " +
                    "You have ${maxErrors.value} errors answering it.")
    }
}


fun ask(flashCards: MutableMap<String, String>, wrongAnswers: MutableMap<String, Int>) {
    log("How many times to ask?")

    val askTimes = getInput().toInt()

    val list = mutableListOf<String>()
    for (i in flashCards) {
        list.add(i.key)
    }
    repeat(askTimes){
        val random = Math.random()
        val term = list[(random * list.size).toInt()]
        val definition = flashCards[term] ?: ""
        log("Print the definition of \"$term\":")

        val answer = getInput()
        checkAnswer(term, definition, answer, flashCards, wrongAnswers)
    }
}

fun export(fileName: String, flashCards: MutableMap<String, String>) {
    val file = File(fileName)
    var cardCount = 0
    var content = ""
    for (card in flashCards) {
        content += card.key
        content += "\n"
        content += card.value
        content += "\n"
        cardCount++
    }
    file.writeText(content)
    log("$cardCount cards have been saved.")
}

fun import (fileName: String, flashCards: MutableMap<String, String>) {
    var cardsLoaded = 0
    var line = 0
    val file = File(fileName)

    if (file.exists()) {
        var term = ""
        File(fileName).forEachLine {
            line++
            if (line % 2 == 1) {
                term = it
            } else {
                val definition = it
                flashCards[term] = definition
                cardsLoaded++
            }
        }
        log("$cardsLoaded cards have been loaded.")
    }
    else
        log("File not found.")
}

fun remove(flashCards: MutableMap<String, String>){
    log("The card:")
    val term = getInput()

    if(flashCards.containsKey(term)) {
        flashCards.remove(term)
        log("The card has been removed.")
    } else {
        log("Can't remove \"$term\": there is no such card.")
    }

}

fun addACard(flashCards: MutableMap<String, String>) {
    log("The card:")
    val term = getInput()
    while (flashCards.containsKey(term)) {
        log("The card \"$term\" already exists.")
        return
    }

    log("The definition of the card:")
    val definition = getInput()
    while(flashCards.containsValue(definition)) {
        log("The definition \"$definition\" already exists.")
        return
    }
    flashCards[term] = definition
    log("The pair (\"$term\":\"$definition\") has been added.")
}

fun getInput(): String {
    val scanner = Scanner(System.`in`)
    val input = scanner.nextLine()
    log(input, false)
    return input
}

fun checkAnswer(term: String, definition: String, answer: String, flashCards: MutableMap<String, String>,
                wrongAnswers: MutableMap<String, Int>) {
    if (definition == answer) {
        log("Correct answer")
    } else {
        wrongAnswers[term] = wrongAnswers[term]?.plus(1) ?: 1
        if(flashCards.containsValue(answer)) {
            var key = flashCards.filterValues { it == answer }.keys.toString()
                    .replace("[","\"")
            key = key.replace("]","\"")
            log("Wrong answer. The correct one is \"$definition\", you've just written" +
                    " the definition of ${key}:")
        } else {
            log("Wrong answer. The correct one is \"$definition\".")
        }
    }
}