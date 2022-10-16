import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.swing.Swing
import org.jetbrains.skija.*
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.SkiaRenderer
import org.jetbrains.skiko.SkiaWindow
import java.awt.Dimension
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionAdapter
import javax.swing.WindowConstants
import kotlin.time.ExperimentalTime

fun main() {
    createWindow("Chekkers")
}

fun createWindow(title: String) = runBlocking(Dispatchers.Swing) {
    val window = SkiaWindow()
    window.defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE
    window.title = title

    window.layer.renderer = Renderer(window.layer)
    window.layer.addMouseMotionListener(MouseMotionAdapter)

    window.preferredSize = Dimension(700, 800)
    window.minimumSize = Dimension(750,850)
    window.pack()
    window.layer.awaitRedraw()
    window.isVisible = true
}
enum class Strokes(){ a, b, c, d, e, f, g, h; }
data class Chekker(val col: String, var x: Int, var y: Int, var lady: Boolean, var north: MutableList<Int> = mutableListOf(),
                   var west: MutableList<Int> = mutableListOf(), var east: MutableList<Int> = mutableListOf(), var south: MutableList<Int> = mutableListOf())
fun initi(col: String): MutableList<Chekker>{
    var comand = mutableListOf<Chekker>()
    if(col == "white") {
        for (i in 0..3) {
            comand += Chekker(col, (i + 1) * 2, 1, false)
            comand += Chekker(col, (i) * 2 + 1, 2, false)
            comand += Chekker(col, (i + 1) * 2, 3, false)
        }
    }
    else{
        for (i in 0..3) {
            comand += Chekker(col, (i) * 2 + 1, 8, false)
            comand += Chekker(col, (i + 1) * 2, 7, false)
            comand += Chekker(col, (i) * 2 + 1, 6, false)
        }
    }
    return comand
}
fun isNeZanyato(pole: Chekker, white: List<Chekker>, black: List<Chekker>): Int{
    if(pole.x > 8 || pole.y > 8 || pole.x < 1 || pole.y < 1)
        return 2
    for(i in white){
        if(pole.x == i.x && pole.y == i.y)
            return 0
    }
    for(i in black){
        if(pole.x == i.x && pole.y == i.y)
            return 1
    }
    return -1
}
fun mojetBitLady(north:MutableList<Int>, j: Int): Boolean{
    for(i in 0 until north.size-1){
        if(north[i] == j && north[i+1] == -1) {
            var k = 0
            for (l in 0 until i) {
                if(north[l] != -1)
                    k++
            }
            if(k == 0){
                return true
            }
        }
    }
    return false
}
fun perescshetLady(chekker: Chekker, white: MutableList<Chekker>, black: MutableList<Chekker>): Chekker{
    var x = chekker.x
    var y = chekker.y
    var north = mutableListOf<Int>()
    var south = mutableListOf<Int>()
    var west = mutableListOf<Int>()
    var east = mutableListOf<Int>()
    while(x > 1 && y > 1){
        north += isNeZanyato(Chekker(" ", x - 1, y - 1, false), white, black)
        x--
        y--
    }
    x = chekker.x
    y = chekker.y
    while(x < 8 && y > 1){
        west += isNeZanyato(Chekker(" ", x + 1, y - 1, false), white, black)
        x++
        y--
    }
    x = chekker.x
    y = chekker.y
    while(x > 1 && y < 8){
        east += isNeZanyato(Chekker(" ", x - 1, y + 1, false), white, black)
        x--
        y++
    }
    x = chekker.x
    y = chekker.y
    while(x < 8 && y < 8){
        south += isNeZanyato(Chekker(" ", x + 1, y + 1, false), white, black)
        x++
        y++
    }
    //println(north)
    //println(west)
    //println(east)
    //println(south)
    return Chekker(chekker.col, chekker.x, chekker.y, chekker.lady, north, west, east, south)
}
fun mojetBit(chekker: Chekker, white: MutableList<Chekker>, black: MutableList<Chekker>, j: Int): Boolean{
    if(chekker.lady) {
        var x = chekker.x
        var y = chekker.y
        var north = mutableListOf<Int>()
        var south = mutableListOf<Int>()
        var west = mutableListOf<Int>()
        var east = mutableListOf<Int>()
        while(x > 1 && y > 1){
            north += isNeZanyato(Chekker(" ", x - 1, y - 1, false), white, black)
            x--
            y--
        }
        x = chekker.x
        y = chekker.y
        while(x < 8 && y > 1){
            west += isNeZanyato(Chekker(" ", x + 1, y - 1, false), white, black)
            x++
            y--
        }
        x = chekker.x
        y = chekker.y
        while(x > 1 && y < 8){
            east += isNeZanyato(Chekker(" ", x - 1, y + 1, false), white, black)
            x--
            y++
        }
        x = chekker.x
        y = chekker.y
        while(x < 8 && y < 8){
            south += isNeZanyato(Chekker(" ", x + 1, y + 1, false), white, black)
            x++
            y++
        }

        return mojetBitLady(north, j) || mojetBitLady(south, j) || mojetBitLady(east, j) || mojetBitLady(west, j)
    }
    return (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y + 1, false), white, black) == j && isNeZanyato(Chekker(" ", chekker.x - 2, chekker.y + 2, false), white, black) == -1) ||
            (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y - 1, false), white, black) == j && isNeZanyato(Chekker(" ", chekker.x - 2, chekker.y - 2, false), white, black) == -1)||
            (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y + 1, false), white, black) == j && isNeZanyato(Chekker(" ", chekker.x + 2, chekker.y + 2, false), white, black) == -1)||
            (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y - 1, false), white, black) == j && isNeZanyato(Chekker(" ", chekker.x + 2, chekker.y - 2, false), white, black) == -1)
}
fun mojetHodit(chekker: Chekker, white: MutableList<Chekker>, black: MutableList<Chekker>): Boolean{
    if(chekker.lady){
        var ch = perescshetLady(chekker, white, black)
        if(ch.north.size > 0 && ch.north[0] == -1) return true
        else if(ch.south.size > 0 && ch.south[0] == -1) return true
        else if(ch.west.size > 0 && ch.west[0] == -1) return true
        else if(ch.east.size > 0 && ch.east[0] == -1) return true
        else return false
    }
    else
        if(chekker.col == "white"){
            if(isNeZanyato(Chekker(" ", chekker.x-1, chekker.y+1, false), white, black) == -1) return true
            else if(isNeZanyato(Chekker(" ", chekker.x+1, chekker.y+1, false), white, black) == -1) return true
            else return false
        }
        else{
            if(isNeZanyato(Chekker(" ", chekker.x-1, chekker.y-1, false), white, black) == -1) return true
            else if(isNeZanyato(Chekker(" ", chekker.x+1, chekker.y-1, false), white, black) == -1) return true
            else return false
        }
}
fun comandMojetHoditW(white: MutableList<Chekker>, black: MutableList<Chekker>): Boolean{
    for(i in white){
        if(mojetBit(i, white, black, 1)) return true
        if(mojetHodit(i, white, black)) return true
    }
    return false
}
fun comandMojetHoditB(white: MutableList<Chekker>, black: MutableList<Chekker>): Boolean{
    for(i in black){
        if(mojetBit(i, white, black, 0)) return true
        if(mojetHodit(i, white, black)) return true
    }
    return false
}


class Renderer(val layer: SkiaLayer): SkiaRenderer {
    val typeface = Typeface.makeFromFile("fonts/JetBrainsMono-Regular.ttf")
    val font = Font(typeface, 28f)
    var white = initi("white")
    var black = initi("black")
    var isit = false
    var hod = 0
    var really = false
    var chosenChecker: Chekker = Chekker("1", 1, 1, false)
    var chosenPole: Chekker = Chekker("1", 1, 1, false)
    var anotherChecker = Chekker("1", 1, 1, false)
    var xbeg: Float = 0f
    var ybeg: Float = 0f
    var xend: Float = 0f
    var yend: Float = 0f
    var xch: Int = 0
    var ych: Int = 0
    var deadChecker = Chekker("1", 1, 1, false)
    var j = -1
    var k = -1
    val paint = Paint().apply {
        color = 0xff9BC730L.toInt()
        mode = PaintMode.FILL
        strokeWidth = 1f
    }

    @ExperimentalTime
    override fun onRender(canvas: Canvas, width: Int, height: Int, nanoTime: Long) {
        val contentScale = layer.contentScale
        canvas.scale(contentScale, contentScale)
        if((hod == 0 && comandMojetHoditW(white, black) || (hod == 1 && comandMojetHoditB(white, black)))){
        for(i in white){
            if(i.y == 8)
                i.lady = true
        }
        for(i in black){
            if(i.y == 1)
                i.lady = true
        }
        if(anotherChecker.col != "1"){
            if (State.click == true && isit == false && hod == 0) {
                isit = true
                xbeg = State.mouseX
                ybeg = State.mouseY
                xch = ((xbeg - 10).toInt()) / 80 + 1
                ych = ((ybeg - 10).toInt()) / 80 + 1
                for (i in 0 until white.size) {
                    if (white[i].x == xch && white[i].y == ych) {
                        chosenChecker = white[i]
                        j = i
                    }
                }
            }
            if (State.click == true && isit == false && hod == 1) {
                isit = true
                xbeg = State.mouseX
                ybeg = State.mouseY
                xch = ((xbeg - 10).toInt()) / 80 + 1
                ych = ((ybeg - 10).toInt()) / 80 + 1
                for (i in 0 until black.size) {
                    if (black[i].x == xch && black[i].y == ych) {
                        chosenChecker = black[i]
                        j = i
                    }
                }
            }


            if(State.click == false && isit == true){
                isit = false
                xend = State.mouseX
                yend = State.mouseY
                xch = ((xend-10).toInt())/80+1
                ych = ((yend-10).toInt())/80+1
                if(xch < 9 &&  ych < 9 && chosenChecker.x == anotherChecker.x && chosenChecker.y == anotherChecker.y)
                    chosenPole = Chekker("0", xch, ych, false)
            }
        }
        else {
            if (State.click == true && isit == false && hod == 0) {
                isit = true
                xbeg = State.mouseX
                ybeg = State.mouseY
                xch = ((xbeg - 10).toInt()) / 80 + 1
                ych = ((ybeg - 10).toInt()) / 80 + 1
                for (i in 0 until white.size) {
                    if (white[i].x == xch && white[i].y == ych) {
                        chosenChecker = white[i]
                        j = i
                    }
                }
            }
            if (State.click == true && isit == false && hod == 1) {
                isit = true
                xbeg = State.mouseX
                ybeg = State.mouseY
                xch = ((xbeg - 10).toInt()) / 80 + 1
                ych = ((ybeg - 10).toInt()) / 80 + 1
                for (i in 0 until black.size) {
                    if (black[i].x == xch && black[i].y == ych) {
                        chosenChecker = black[i]
                        j = i
                    }
                }
            }
            if (State.click == false && isit == true) {
                isit = false
                xend = State.mouseX
                yend = State.mouseY
                xch = ((xend - 10).toInt()) / 80 + 1
                ych = ((yend - 10).toInt()) / 80 + 1
                if(xch < 9 &&  ych < 9)
                    chosenPole = Chekker("0", xch, ych, false)
            }
        }
        if((hod == 0 && chosenChecker.col != "white")||(hod == 1 && chosenChecker.col != "black")){
            chosenChecker = Chekker("1", 1, 1, false)
            chosenPole = Chekker("1", 1, 1, false)
        }
        if(hod == 0){
            for(i in white){
                if(mojetBit(i, white, black, 1)){
                    really = true
                }
            }
            if(!mojetBit(chosenChecker, white, black, 1) && really){
                chosenChecker = Chekker("1", 1, 1, false)
                chosenPole = Chekker("1", 1, 1, false)
            }
            really = false
        }
        if(hod == 1){
            for(i in black){
                if(mojetBit(i, white, black, 0)){
                    really = true
                }
            }
            if(!mojetBit(chosenChecker, white, black, 0) && really){
                chosenChecker = Chekker("1", 1, 1, false)
                chosenPole = Chekker("1", 1, 1, false)
            }
            really = false
        }
        if(chosenChecker.col == "white" && chosenPole.col == "0" && isNeZanyato(chosenPole, white, black) == -1){
            if (!chosenChecker.lady) {
                if (mojetBit(chosenChecker, white, black, 1)) {
                    if ((chosenPole.x == chosenChecker.x + 2 || chosenPole.x == chosenChecker.x - 2) &&
                        (chosenPole.y == chosenChecker.y + 2 || chosenPole.y == chosenChecker.y - 2)) {
                        deadChecker = Chekker("0", (chosenChecker.x+chosenPole.x)/2, (chosenChecker.y+chosenPole.y)/2, false)
                        white[j].x = chosenPole.x
                        white[j].y = chosenPole.y
                        for (i in 0 until black.size) {
                            if (deadChecker.x == black[i].x && deadChecker.y == black[i].y)
                                k = i
                        }
                        black.removeAt(k)
                        chosenChecker = chosenPole
                        if (mojetBit(chosenChecker, white, black, 1))
                            anotherChecker = white[j]
                        else{
                            hod = 1
                            anotherChecker = Chekker("1", 1, 1, false)
                        }
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                    else{
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                }
                else{
                    if((chosenChecker.x == chosenPole.x-1 || chosenChecker.x == chosenPole.x+1) && chosenChecker.y == chosenPole.y-1){
                        white[j].x = chosenPole.x
                        white[j].y = chosenPole.y
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                        hod = 1
                    }
                }
            }
            else{
                if(mojetBit(chosenChecker, white, black, 1)){
                    var b = false
                    chosenChecker = perescshetLady(chosenChecker, white, black)
                    chosenPole = perescshetLady(chosenPole, white, black)
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.north.size >0){
                        var may = -(chosenPole.x - chosenChecker.x)
                        if(may > 0){
                            var g = 0
                            while(g < chosenChecker.north.size && chosenChecker.north[g] == -1) g++
                            var dead = g
                            g++
                            while(g < chosenChecker.north.size && chosenChecker.north[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x-dead-1, chosenChecker.y-dead-1, false)
                                for (i in 0 until black.size) {
                                    if (deadChecker.x == black[i].x && deadChecker.y == black[i].y)
                                        k = i
                                }
                                black.removeAt(k)
                            }
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.south.size > 0){
                        var may = chosenPole.x - chosenChecker.x
                        if(may > 0) {
                            var g = 0
                            while (g < chosenChecker.south.size && chosenChecker.south[g] == -1) g++
                            var dead = g
                            g++
                            while (g < chosenChecker.south.size && chosenChecker.south[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x+dead+1, chosenChecker.y+dead+1, false)
                                for (i in 0 until black.size) {
                                    if (deadChecker.x == black[i].x && deadChecker.y == black[i].y)
                                        k = i
                                }
                                black.removeAt(k)
                            }
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.west.size > 0){
                        var may = chosenPole.x - chosenChecker.x
                        if(may > 0) {
                            var g = 0
                            while (g < chosenChecker.west.size && chosenChecker.west[g] == -1) g++
                            var dead = g
                            g++
                            while (g < chosenChecker.west.size && chosenChecker.west[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x+dead+1, chosenChecker.y-dead-1, false)
                                for (i in 0 until black.size) {
                                    if (deadChecker.x == black[i].x && deadChecker.y == black[i].y)
                                        k = i
                                }
                                black.removeAt(k)
                            }
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.east.size > 0){
                        var may = -(chosenPole.x - chosenChecker.x)
                        if(may>0) {
                            var g = 0
                            while (g < chosenChecker.east.size && chosenChecker.east[g] == -1) g++
                            var dead = g
                            g++
                            while (g < chosenChecker.east.size && chosenChecker.east[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x-dead-1, chosenChecker.y+dead+1, false)
                                for (i in 0 until black.size) {
                                    if (deadChecker.x == black[i].x && deadChecker.y == black[i].y)
                                        k = i
                                }
                                black.removeAt(k)
                            }
                        }
                    }
                    if(b) {
                        white[j].x = chosenPole.x
                        white[j].y = chosenPole.y
                        white[j].north = chosenPole.north
                        white[j].west = chosenPole.west
                        white[j].east = chosenPole.east
                        white[j].south = chosenPole.south
                        chosenChecker = chosenPole
                        if (mojetBit(white[j], white, black, 1)) {
                            anotherChecker = white[j]
                        }
                        else{
                            hod = 1
                            anotherChecker = Chekker("1", 1, 1, false)
                        }
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                    else{
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                }
                else if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y || chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y)){
                    chosenChecker = perescshetLady(chosenChecker, white, black)
                    chosenPole = perescshetLady(chosenPole, white, black)
                    var b = true
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.x-chosenPole.x > 0 &&
                        chosenChecker.x-chosenPole.x <= chosenChecker.north.size){
                        for(i in 0 until chosenChecker.x-chosenPole.x){
                            if(chosenChecker.north[i] != -1) b = false
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.x-chosenPole.x < 0 &&
                        chosenChecker.x-chosenPole.x <= chosenChecker.south.size){
                        for(i in 0 until chosenChecker.x-chosenPole.x){
                            if(chosenChecker.south[i] != -1) b = false
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.x-chosenPole.x < 0 &&
                        chosenPole.x - chosenChecker.x <= chosenChecker.west.size){
                        for(i in 0 until chosenPole.x - chosenChecker.x){
                            if(chosenChecker.west[i] != -1) b = false
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.x-chosenPole.x > 0 &&
                        chosenChecker.x-chosenPole.x <= chosenChecker.east.size){
                        for(i in 0 until chosenChecker.x-chosenPole.x){
                            if(chosenChecker.east[i] != -1) b = false
                        }
                    }
                    if(!b){
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                    else{
                        white[j].x = chosenPole.x
                        white[j].y = chosenPole.y
                        white[j].north = chosenPole.north
                        white[j].west = chosenPole.west
                        white[j].east = chosenPole.east
                        white[j].south = chosenPole.south
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                        hod = 1
                    }
                }
                else{
                    chosenChecker = Chekker("1", 1, 1, false)
                    chosenPole = Chekker("1", 1, 1, false)
                }
            }
        }
        if(chosenChecker.col == "black" && chosenPole.col == "0" && isNeZanyato(chosenPole, white, black) == -1){
            if (!chosenChecker.lady) {
                if (mojetBit(chosenChecker, white, black, 0)) {
                    if ((chosenPole.x == chosenChecker.x + 2 || chosenPole.x == chosenChecker.x - 2) &&
                        (chosenPole.y == chosenChecker.y + 2 || chosenPole.y == chosenChecker.y - 2)) {
                        deadChecker = Chekker("0", (chosenChecker.x+chosenPole.x)/2, (chosenChecker.y+chosenPole.y)/2, false)
                        black[j].x = chosenPole.x
                        black[j].y = chosenPole.y
                        for (i in 0 until white.size) {
                            if (deadChecker.x == white[i].x && deadChecker.y == white[i].y)
                                k = i
                        }
                        white.removeAt(k)
                        chosenChecker = chosenPole
                        if (mojetBit(chosenChecker, white, black, 0))
                            anotherChecker = black[j]
                        else{
                            hod = 0
                            anotherChecker = Chekker("1", 1, 1, false)
                        }
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                    else{
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                }
                else{
                    if((chosenChecker.x == chosenPole.x-1 || chosenChecker.x == chosenPole.x+1) && chosenChecker.y == chosenPole.y+1){
                        black[j].x = chosenPole.x
                        black[j].y = chosenPole.y
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                        hod = 0
                    }
                }
            }
            else{
                if(mojetBit(chosenChecker, white, black, 0)){
                    var b = false
                    chosenChecker = perescshetLady(chosenChecker, white, black)
                    chosenPole = perescshetLady(chosenPole, white, black)
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.north.size >0){
                        var may = -(chosenPole.x - chosenChecker.x)
                        if(may > 0){
                            var g = 0
                            while(g < chosenChecker.north.size && chosenChecker.north[g] == -1) g++
                            var dead = g
                            g++
                            while(g < chosenChecker.north.size && chosenChecker.north[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x-dead-1, chosenChecker.y-dead-1, false)
                                for (i in 0 until white.size) {
                                    if (deadChecker.x == white[i].x && deadChecker.y == white[i].y)
                                        k = i
                                }
                                white.removeAt(k)
                            }
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.south.size > 0){
                        var may = chosenPole.x - chosenChecker.x
                        if(may > 0) {
                            var g = 0
                            while (g < chosenChecker.south.size && chosenChecker.south[g] == -1) g++
                            var dead = g
                            g++
                            while (g < chosenChecker.south.size && chosenChecker.south[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x+dead+1, chosenChecker.y+dead+1, false)
                                for (i in 0 until white.size) {
                                    if (deadChecker.x == white[i].x && deadChecker.y == white[i].y)
                                        k = i
                                }
                                white.removeAt(k)
                            }
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.west.size > 0){
                        var may = chosenPole.x - chosenChecker.x
                        if(may > 0) {
                            var g = 0
                            while (g < chosenChecker.west.size && chosenChecker.west[g] == -1) g++
                            var dead = g
                            g++
                            while (g < chosenChecker.west.size && chosenChecker.west[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x+dead+1, chosenChecker.y-dead-1, false)
                                for (i in 0 until white.size) {
                                    if (deadChecker.x == white[i].x && deadChecker.y == white[i].y)
                                        k = i
                                }
                                white.removeAt(k)
                            }
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.east.size > 0){
                        var may = -(chosenPole.x - chosenChecker.x)
                        if(may>0) {
                            var g = 0
                            while (g < chosenChecker.east.size && chosenChecker.east[g] == -1) g++
                            var dead = g
                            g++
                            while (g < chosenChecker.east.size && chosenChecker.east[g] == -1) g++
                            if(g >=may && may > dead) {
                                b = true
                                deadChecker = Chekker("0", chosenChecker.x-dead-1, chosenChecker.y+dead+1, false)
                                for (i in 0 until white.size) {
                                    if (deadChecker.x == white[i].x && deadChecker.y == white[i].y)
                                        k = i
                                }
                                white.removeAt(k)
                            }
                        }
                    }
                    if(b) {
                        black[j].x = chosenPole.x
                        black[j].y = chosenPole.y
                        black[j].north = chosenPole.north
                        black[j].west = chosenPole.west
                        black[j].east = chosenPole.east
                        black[j].south = chosenPole.south
                        chosenChecker = chosenPole
                        if (mojetBit(black[j], white, black, 0)) {
                            anotherChecker = black[j]
                        }
                        else{
                            hod = 0
                            anotherChecker = Chekker("1", 1, 1, false)
                        }
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                    else{
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }
                }
                else if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y || chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y)){
                    chosenChecker = perescshetLady(chosenChecker, white, black)
                    chosenPole = perescshetLady(chosenPole, white, black)
                    var b = true
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.x-chosenPole.x > 0 &&
                        chosenChecker.x-chosenPole.x <= chosenChecker.north.size){
                        for(i in 0 until chosenChecker.x-chosenPole.x){
                            if(chosenChecker.north[i] != -1) b = false
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == chosenPole.y - chosenChecker.y && chosenChecker.x-chosenPole.x < 0 &&
                        chosenPole.x - chosenChecker.x <= chosenChecker.south.size){
                        for(i in 0 until chosenPole.x - chosenChecker.x){
                            if(chosenChecker.south[i] != -1) b = false
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.x-chosenPole.x < 0 &&
                        chosenPole.x - chosenChecker.x <= chosenChecker.west.size){
                        for(i in 0 until chosenPole.x - chosenChecker.x){
                            if(chosenChecker.west[i] != -1) b = false
                        }
                    }
                    if(chosenPole.x - chosenChecker.x == -(chosenPole.y - chosenChecker.y) && chosenChecker.x-chosenPole.x > 0 &&
                        chosenChecker.x-chosenPole.x <= chosenChecker.east.size){
                        for(i in 0 until chosenChecker.x-chosenPole.x){
                            if(chosenChecker.east[i] != -1) b = false
                        }
                    }
                    if(b) {
                        black[j].x = chosenPole.x
                        black[j].y = chosenPole.y
                        black[j].north = chosenPole.north
                        black[j].west = chosenPole.west
                        black[j].east = chosenPole.east
                        black[j].south = chosenPole.south
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                        hod = 0
                    }
                    else{
                        chosenChecker = Chekker("1", 1, 1, false)
                        chosenPole = Chekker("1", 1, 1, false)
                    }

                }
                else{
                    chosenChecker = Chekker("1", 1, 1, false)
                    chosenPole = Chekker("1", 1, 1, false)
                }
            }
        }
        displayCheyHod(canvas, hod)
        displayBoardFace(canvas)
        displayCheckers(canvas)
        displayKray(canvas)
        if(chosenChecker.col != "1")
            mayMove(canvas, chosenChecker)
        }
        else{
            displayWinner(canvas, hod)
        }
        layer.needRedraw()
    }
    private fun displayWinner(canvas: Canvas, hod: Int){
        canvas.drawRect(Rect.makeXYWH(100f, 100f, 480f, 300f), Paint().apply { color = 0xFFFF4500.toInt() })
        canvas.drawRect(Rect.makeXYWH(120f, 120f, 440f, 260f), Paint().apply { color = 0xFFADFF2F.toInt() })
        if(hod == 1) {
            chekker(canvas, "white", 140f, 210f, false)
            canvas.drawString(" Белые победили", 240f, 260f, font, Paint().apply { color = 0xFF228B22.toInt() })
        }
        if(hod == 0) {
            chekker(canvas, "black", 140f, 210f, false)
            canvas.drawString(" Черные победили", 240f, 260f, font, Paint().apply { color = 0xFF228B22.toInt() })
        }
    }
    private fun displayKray(canvas: Canvas) {
        for(i in 1..8){
            canvas.drawString(i.toString(), 40f+80*(i-1), 675f, font, paint)
        }
        for(i in Strokes.a.ordinal..Strokes.h.ordinal){
            canvas.drawString(Strokes.values()[i].toString(), 660f, 60f+80*(i), font, paint)
        }
    }
    private fun displayCheyHod(canvas: Canvas, hod: Int){
        if(hod == 0)
            canvas.drawString("Ходят белые", 200f, 700f, font, Paint().apply { color = 0xFF138353.toInt() })
        else
            canvas.drawString("Ходят черные", 200f, 700f, font, Paint().apply { color = 0xFF138353.toInt() })
    }
    private fun displayBoardFace(canvas: Canvas) {
        val x = 10f
        val y = 10f
        for(i in 0..6){
            for(j in 0..6){
                    canvas.drawRect(Rect.makeXYWH(x+80*i + 80*(j%2), y+80*(i%2)+80*j, 80f, 80f), Paint().apply { color = 0xFFEBA16C.toInt() })
            }
        }
        canvas.drawRect(Rect.makeXYWH(x+7*80, y+7*80, 80f, 80f), Paint().apply { color = 0xFFEBA16C.toInt() })
        for(i in 0..6){
            for(j in 0..6){
                canvas.drawRect(Rect.makeXYWH(x+560-(80*i + 80*(j%2)), y+(80*(i%2)+80*j), 80f, 80f), Paint().apply { color = 0xFFD2691E.toInt() })
            }
        }
        canvas.drawRect(Rect.makeXYWH(x, y+560, 80f, 80f), Paint().apply { color = 0xFFD2691E.toInt() })

    }
    private fun displayCheckers(canvas: Canvas) {
        for(chek in white) {
            chekker(canvas, chek.col, 10f+(chek.x-1)*80, 10f+(chek.y-1)*80, chek.lady)
        }
        for(chek in black) {
            chekker(canvas, chek.col, 10f+(chek.x-1)*80, 10f+(chek.y-1)*80, chek.lady)
        }
    }
    private fun mayMove(canvas: Canvas, chekker: Chekker){
        if(chekker.col == "white") {
            if (chekker.lady) {
                var chekker1 = perescshetLady(chekker, white, black)
                if(!mojetBit(chekker1, white, black, 1)){
                    var g = 0
                    while(g < chekker1.north.size && chekker1.north[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                    g = 0
                    while(g < chekker1.south.size && chekker1.south[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x +g), 10f + 80 * (chekker.y +g), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                    g = 0
                    while(g < chekker1.west.size && chekker1.west[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x + g), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                    g = 0
                    while(g < chekker1.east.size && chekker1.east[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y +g), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                }
                else{
                    var g = 0
                    while(g < chekker1.east.size && chekker1.east[g] == -1) g++
                    if(g < chekker1.east.size && chekker1.east[g] == 1){
                        g++
                        while(g < chekker1.east.size && chekker1.east[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y +g), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                    g = 0
                    while(g < chekker1.north.size && chekker1.north[g] == -1) g++
                    if(g < chekker1.north.size && chekker1.north[g] == 1){
                        g++
                        while(g < chekker1.north.size && chekker1.north[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                    g = 0
                    while(g < chekker1.south.size && chekker1.south[g] == -1) g++
                    if(g < chekker1.south.size && chekker1.south[g] == 1){
                        g++
                        while(g < chekker1.south.size && chekker1.south[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x +g), 10f + 80 * (chekker.y +g), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                    g = 0
                    while(g < chekker1.west.size && chekker1.west[g] == -1) g++
                    if(g < chekker1.west.size && chekker1.west[g] == 1){
                        g++
                        while(g < chekker1.west.size && chekker1.west[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x + g), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                }
            }
            else {
                if(!mojetBit(chekker, white, black, 1)) {
                    if (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y + 1, false), white, black) == -1)
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - 2), 10f + 80 * (chekker.y), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y + 1, false), white, black) == -1)
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x), 10f + 80 * (chekker.y), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                }
                else {
                    if (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y + 1, false), white, black) == 1 &&
                        isNeZanyato(Chekker(" ", chekker.x - 2, chekker.y + 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - 3), 10f + 80 * (chekker.y + 1), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y + 1, false), white, black) == 1 &&
                        isNeZanyato(Chekker(" ", chekker.x + 2, chekker.y + 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x + 1), 10f + 80 * (chekker.y + 1), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y - 1, false), white, black) == 1 &&
                        isNeZanyato(Chekker(" ", chekker.x + 2, chekker.y - 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x + 1), 10f + 80 * (chekker.y - 3), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y - 1, false), white, black) == 1 &&
                        isNeZanyato(Chekker(" ", chekker.x - 2, chekker.y - 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - 3), 10f + 80 * (chekker.y - 3), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                }
            }
        }
        else{
            if (chekker.lady) {
                var chekker1 = perescshetLady(chekker, white, black)
                if(!mojetBit(chekker1, white, black, 0)){
                    var g = 0
                    while(g < chekker1.north.size && chekker1.north[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                    g = 0
                    while(g < chekker1.south.size && chekker1.south[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x +g), 10f + 80 * (chekker.y +g), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                    g = 0
                    while(g < chekker1.west.size && chekker1.west[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x + g), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                    g = 0
                    while(g < chekker1.east.size && chekker1.east[g] == -1){
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y +g), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                        g++
                    }
                }
                else{
                    var g = 0
                    while(g < chekker1.east.size && chekker1.east[g] == -1) g++
                    if(g < chekker1.east.size && chekker1.east[g] == 0){
                        g++
                        while(g < chekker1.east.size && chekker1.east[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y +g), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                    g = 0
                    while(g < chekker1.north.size && chekker1.north[g] == -1) g++
                    if(g < chekker1.north.size && chekker1.north[g] == 0){
                        g++
                        while(g < chekker1.north.size && chekker1.north[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x - g - 2), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                    g = 0
                    while(g < chekker1.south.size && chekker1.south[g] == -1) g++
                    if(g < chekker1.south.size && chekker1.south[g] == 0){
                        g++
                        while(g < chekker1.south.size && chekker1.south[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x +g), 10f + 80 * (chekker.y +g), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                    g = 0
                    while(g < chekker1.west.size && chekker1.west[g] == -1) g++
                    if(g < chekker1.west.size && chekker1.west[g] == 0){
                        g++
                        while(g < chekker1.west.size && chekker1.west[g] == -1){
                            canvas.drawRect(
                                Rect.makeXYWH(10f + 80 * (chekker.x + g), 10f + 80 * (chekker.y - g - 2), 80f, 80f),
                                Paint().apply { color = 0x77FFAA00.toInt() })
                            g++
                        }
                    }
                }
            }
            else {
                if (!mojetBit(chekker, white, black, 0)) {
                    if (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y - 1, false), white, black) == -1)
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - 2), 10f + 80 * (chekker.y - 2), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y - 1, false), white, black) == -1)
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x), 10f + 80 * (chekker.y - 2), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                } else {
                    if (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y + 1, false), white, black) == 0 &&
                        isNeZanyato(Chekker(" ", chekker.x - 2, chekker.y + 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - 3), 10f + 80 * (chekker.y + 1), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y + 1, false), white, black) == 0 &&
                        isNeZanyato(Chekker(" ", chekker.x + 2, chekker.y + 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x + 1), 10f + 80 * (chekker.y + 1), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x + 1, chekker.y - 1, false), white, black) == 0 &&
                        isNeZanyato(Chekker(" ", chekker.x + 2, chekker.y - 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x + 1), 10f + 80 * (chekker.y - 3), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                    if (isNeZanyato(Chekker(" ", chekker.x - 1, chekker.y - 1, false), white, black) == 0 &&
                        isNeZanyato(Chekker(" ", chekker.x - 2, chekker.y - 2, false), white, black) == -1
                    )
                        canvas.drawRect(
                            Rect.makeXYWH(10f + 80 * (chekker.x - 3), 10f + 80 * (chekker.y - 3), 80f, 80f),
                            Paint().apply { color = 0x77FFAA00.toInt() })
                }
            }
        }
    }
    private fun chekker(canvas: Canvas, col: String, x: Float, y: Float, lady: Boolean) {
        if(col == "black") {
            if(lady){
                canvas.drawOval(Rect.makeXYWH(x, y, 80f, 80f), Paint().apply { color = 0xFF202020.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 5, y + 5, 70f, 70f), Paint().apply { color = 0xFF101010.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 10, y + 10, 60f, 60f), Paint().apply { color = 0xFFFFFFFF.toInt() })
            }
            else {
                canvas.drawOval(Rect.makeXYWH(x, y, 80f, 80f), Paint().apply { color = 0xFF202020.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 5, y + 5, 70f, 70f), Paint().apply { color = 0xFF101010.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 10, y + 10, 60f, 60f), Paint().apply { color = 0xFF000000.toInt() })
            }
        }
        if(col == "white") {
            if(lady){
                canvas.drawOval(Rect.makeXYWH(x, y, 80f, 80f), Paint().apply { color = 0xFFA0A0A0.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 5, y + 5, 70f, 70f), Paint().apply { color = 0xFFC0C0C0.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 10, y + 10, 60f, 60f), Paint().apply { color = 0xFF000000.toInt() })
            }
            else {
                canvas.drawOval(Rect.makeXYWH(x, y, 80f, 80f), Paint().apply { color = 0xFFA0A0A0.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 5, y + 5, 70f, 70f), Paint().apply { color = 0xFFC0C0C0.toInt() })
                canvas.drawOval(Rect.makeXYWH(x + 10, y + 10, 60f, 60f), Paint().apply { color = 0xFFF0F0F0.toInt() })
            }
        }
    }
}
object State {
    var mouseX = 0f
    var mouseY = 0f
    var click = false
}
object MouseMotionAdapter : MouseMotionAdapter() {
    override fun mouseDragged(event: MouseEvent) {
        State.click = true
        State.mouseX = event.x.toFloat()
        State.mouseY = event.y.toFloat()
    }
    override fun mouseMoved(event: MouseEvent) {
        State.click = false
        State.mouseX = event.x.toFloat()
        State.mouseY = event.y.toFloat()
    }
}