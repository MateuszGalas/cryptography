package cryptography

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

// Encrypting message with password using operator xor
fun encryptMessage(): String {
    println("Message to hide:")
    val message = changeToBinary(readln())
    println("Password:")
    val password = changeToBinary(readln())

    var encryptedMessage = ""
    var counter = 0
    // Encrypting
    message.forEach {
        encryptedMessage += it.digitToInt() xor password[counter].digitToInt()
        if (counter == password.length - 1) counter = 0 else counter++
    }

    // Adding stop value to the end of encrypted message
    return encryptedMessage + "000000000000000000000011"
}

// Changing format from String text to string '00110101...'
fun changeToBinary(text: String): String {
    return text.encodeToByteArray().joinToString("") {
        String.format("%08d", it.toString(2).toInt())
    }
}


// Hide encrypted message into image
fun hide() {
    println("Input image file:")
    val inputImage = File(readln())
    println("Output image file:")
    val outputImage = File(readln())

    val message = encryptMessage()

    if (!inputImage.exists()) println("Can't read input file!").also { return }
    if (!outputImage.exists()) outputImage.createNewFile()

    val image = ImageIO.read(inputImage)
    val myOutput = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    if ((message.length + 3) * 8 > image.width * image.height) {
        println("The input image is not large enough to hold this message.")
        return
    }
    var counter = 0
    // Change the least significant bit for blue with message bits and save it to image
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val i = Color(image.getRGB(x, y))

            if (counter < message.length) {
                val color = Color(
                    i.red, i.green,
                    if (i.blue.takeLowestOneBit() == 1) i.blue + if (message[counter].digitToInt() == 1) 0 else -1
                    else i.blue or message[counter].digitToInt()
                )
                myOutput.setRGB(x, y, color.rgb)
                counter++
            } else {
                myOutput.setRGB(x, y, i.rgb)
            }
        }
    }
    ImageIO.write(myOutput, "png", outputImage)
    println("Message saved in ${outputImage.toString().replace("\\", "/")} image.")
}

fun decryptMessage(encryptedMessage: String): String {
    println("Password:")
    val password = changeToBinary(readln())
    var message = ""
    var counter = 0
    // Decrypting message using password na operator xor
    encryptedMessage.forEach {
        if (it != ' ') {
            message += it.digitToInt() xor password[counter].digitToInt()
            if (counter == password.length - 1) counter = 0 else counter++
        } else {
            message += it
        }
    }
    // Change from binary to characters form
    message = message.split(" ")
        .filter { it != "" }
        .map { it.toInt(2).toChar() }
        .joinToString("")
    return message
}
fun show() {
    println("Input image file:")
    val imageFile = File(readln())
    val image = ImageIO.read(imageFile)

    var text = ""
    var encryptedMessage = ""
    for (y in 0 until image.height) {
        for (x in 0 until image.width) {
            val i = Color(image.getRGB(x, y))
            text += if (i.blue.takeLowestOneBit() == 1) 1 else 0

            if (text.length == 8) {
                encryptedMessage += "$text "
                text = ""
            }
            if (encryptedMessage.contains("00000000 00000000 00000011")) {
                encryptedMessage = encryptedMessage.replace("00000000 00000000 00000011", "")
                println("Message:\n${decryptMessage(encryptedMessage)}")
                return
            }
        }
    }
}

fun main() {
    while (true) {
        println("Task (hide, show, exit):")
        when (val action = readln()) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> break
            else -> println("Wrong task: $action")
        }
    }
    println("Bye!")
}