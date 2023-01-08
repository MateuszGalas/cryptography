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

    if (!inputImage.exists()) println("Can't read input file!").also { return }
    if (!outputImage.exists()) outputImage.createNewFile()

    val image = ImageIO.read(inputImage)
    val myOutput = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    // Change the least significant bit for red, green and blue to 1
    for (x in 0 until image.width) {
        for (y in 0 until image.height) {
            val i = Color(image.getRGB(x, y))
            val color = Color(i.red or 1,i.green or 1, i.blue or 1 )

            myOutput.setRGB(x, y, color.rgb)
        }
    }
    ImageIO.write(myOutput, "png", outputImage)

    // Replace to pass the tests
    println("Input Image: ${inputImage.toString().replace("\\", "/")}\n" +
            "Output Image: ${outputImage.toString().replace("\\", "/")}")
    println("Image ${outputImage.toString().replace("\\", "/")} is saved.")
}

fun show() {
    println("Obtaining message from image.")
}

fun main() {
    while(true) {
        println("Task (hide, show, exit):")
        when(val action = readln()) {
            "hide" -> hide()
            "show" -> show()
            "exit" -> break
            else -> println("Wrong task: $action")
        }
    }
    println("Bye!")
}

