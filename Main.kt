package cryptography

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun hide() {
    println("Input image file:")
    val inputImage = File(readln())
    println("Output image file:")
    val outputImage = File(readln())
    println("Message to hide:")
    val message = readln().encodeToByteArray().plus(listOf(0, 0, 3)).joinToString("") {
        String.format("%08d", it.toString(2).toInt())
    }

    if (!inputImage.exists()) println("Can't read input file!").also { return }
    if (!outputImage.exists()) outputImage.createNewFile()

    val image = ImageIO.read(inputImage)
    val myOutput = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    if (message.length * 8 > image.width * image.height) {
        println("The input image is not large enough to hold this message.")
        return
    }
    //println(message)
    var counter = 0
    // Change the least significant bit for red, green and blue to 1
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val i = Color(image.getRGB(x, y))

            if (counter < message.length) {
                val color = Color(
                    i.red, i.green,
                    if (i.blue.takeLowestOneBit() == 1) i.blue and message[counter].digitToInt()
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

    // Replace to pass the tests
    /*println(
        "Input Image: ${inputImage.toString().replace("\\", "/")}\n" +
                "Output Image: ${outputImage.toString().replace("\\", "/")}"
    )*/
    println("Message saved in ${outputImage.toString().replace("\\", "/")} image.")
}

fun show() {
    println("Input image file:")
    val imageFile = File(readln())
    val image = ImageIO.read(imageFile)
    var text = ""
    var message = ""
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val i = Color(image.getRGB(x, y))
            text += if (i.blue.takeLowestOneBit() == 1) 1 else 0

            if (text.length == 8) {
                message += "${text.toInt()} "
                text = ""
            }
            if (message.contains("0 0 11")) {
                message = message.replace("0 0 11", "")
                message = message.split(" ")
                    .filter { it != "" }
                    .map { it.toInt(2).toChar() }
                    .joinToString("")
                println("Message:\n$message")
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

