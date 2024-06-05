package logic

import elements.*
import publicApi.AtttPlayer
import utilities.Log

/**
 * represents the area/space where all players' marks are placed and exist through one active game session
 */
@Suppress("UNUSED_PARAMETER")
internal class GameField(
    internal var sideLength: Int, // the only required parameter
    dimensions: Int = MIN_GAME_FIELD_DIMENSIONS, // simplest variant of a 2d game
    numberOfPlayers: Int = MIN_NUMBER_OF_PLAYERS, // this is obvious, can't be less
) {
    private val minIndex = 0 // this is obvious but let it be here for consistency
    private val maxIndex = sideLength - 1 // constant for the given game field
    private val theMap: MutableMap<Coordinates, AtttPlayer> = mutableMapOf() // needs to be configured later

    init {
        // here we're doing possible corrections that may be needed to keep the game rules reasonable
        if (sideLength > MAX_GAME_FIELD_SIDE_SIZE) sideLength = MAX_GAME_FIELD_SIDE_SIZE
        else if (sideLength < MIN_GAME_FIELD_SIDE_SIZE) sideLength = MIN_GAME_FIELD_SIDE_SIZE
        // let's NOT initialize the initial field for the game to save memory & speed-up new game start
    }

    /**
     * returns beautiful & simple String representation of the current state of game field
     */
    internal fun prepareForPrintingIn2d(): String {
        val sb: StringBuilder = StringBuilder(sideLength * (sideLength + 1))
        for (y in 0 until sideLength) {
            sb.append("\n")
            for (x in 0 until sideLength) {
                sb.append(theMap[Coordinates(x, y)]?.getSymbol() ?: SYMBOL_FOR_ABSENT_MARK).append(' ')
            }
        }
        return sb.toString()
    }

    /**
     * detects if given coordinates are correct in the currently active game field
     */
    internal fun isCorrectPosition(x: Int, y: Int): Boolean = x in 0 until sideLength && y in 0 until sideLength

    /**
     * allows to see what's inside this game field space for the given coordinates
     */
    internal fun getCurrentMarkAt(x: Int, y: Int): AtttPlayer? = theMap[Coordinates(x, y)]

    internal fun clear() = theMap.clear()

    /**
     * ensures that the game field has correct size & is clear, so it is safe to use it for a new game
     */
    internal fun isReady(): Boolean =
        sideLength in MIN_GAME_FIELD_SIDE_SIZE..MAX_GAME_FIELD_SIDE_SIZE && theMap.isEmpty()

    internal fun placeNewMark(where: Coordinates, what: AtttPlayer): Boolean =
        if (theMap[where] == Player.None || theMap[where] == null) {
            theMap[where] = what
            true // new mark is successfully placed
        } else {
            Log.pl("attempting to set a mark for player $what on the occupied coordinates: $where")
            // later we can also emit a custom exception here - to be caught on the UI side and ask for another point
            false // new mark is not placed because the space has been already occupied
        }

    internal fun detectAllExistingLineDirectionsFromThePlacedMark(fromWhere: Coordinates): List<LineDirection> {
        val x = fromWhere.x
        val y = fromWhere.y
        Log.pl("checkPlacedMarkArea: x, y = $x, $y")
        val what = getCurrentMarkAt(x, y) ?: return emptyList()
        val allDirections = mutableListOf<LineDirection>()
        LineDirection.entries
            .filter { it != LineDirection.None }
            .forEach { lineDirection ->
                val newX = x + lineDirection.dx
                val newY = y + lineDirection.dy
                if (isCorrectPosition(newX, newY) && checkIf2MarksAreOfTheSamePlayer(newX, newY, what)) {
                    allDirections.add(lineDirection)
                    Log.pl("line exists in direction: $lineDirection")
                }
            }
        return allDirections
    }

    private fun checkIf2MarksAreOfTheSamePlayer(x: Int, y: Int, what: AtttPlayer) =
        what == theMap[Coordinates(x, y)]

    internal fun measureFullLengthForExistingLineFrom(start: Coordinates, lineDirection: LineDirection): Int {
        // here we already have a detected line of 2 minimum dots, now let's measure its full potential length.
        // we also have a proven placed dot of the same player in the detected line direction.
        // so, we only have to inspect next potential dot of the same direction -> let's prepare the coordinates:
        val checkedNearCoordinates = getTheNextSafeSpaceFor(start, lineDirection)
        var lineTotalLength = 0
        if (checkedNearCoordinates is Coordinates) {
            lineTotalLength =
                measureLineFrom(checkedNearCoordinates, lineDirection, 2) +
                        measureLineFrom(start, lineDirection.opposite(), 0)
            Log.pl("makeNewMove: lineTotalLength = $lineTotalLength")
        } // else checkedNearCoordinates cannot be Border or anything else apart from Coordinates type
        return lineTotalLength
    }

    internal fun measureLineFrom(givenMark: Coordinates, lineDirection: LineDirection, startingLength: Int): Int {
        Log.pl("measureLineFrom: given startingLength: $startingLength")
        Log.pl("measureLineFrom: given start coordinates: $givenMark")
        // firstly let's measure in the given direction and then in the opposite, also recursively
        val nextMark = getTheNextSafeSpaceFor(givenMark, lineDirection)
        Log.pl("measureLineFrom: detected next coordinates: $nextMark")
        return if (nextMark is Coordinates && theMap[givenMark] == theMap[nextMark]) {
            measureLineFrom(nextMark, lineDirection, startingLength + 1)
        } else {
            Log.pl("measureLineFrom: ELSE -> exit: $startingLength")
            startingLength
        }
    }

    internal fun getTheNextSafeSpaceFor(start: Coordinates, lineDirection: LineDirection): GameSpace {
        @Suppress("SimplifyBooleanWithConstants")
        when {
            false || // just for the following cases' alignment
                    start.x <= minIndex && lineDirection == LineDirection.XmYm || // X is out of game field
                    start.x <= minIndex && lineDirection == LineDirection.XmY0 || // X is out of game field
                    start.x <= minIndex && lineDirection == LineDirection.XmYp || // X is out of game field
                    start.y <= minIndex && lineDirection == LineDirection.XmYm || // Y is out of game field
                    start.y <= minIndex && lineDirection == LineDirection.X0Ym || // Y is out of game field
                    start.y <= minIndex && lineDirection == LineDirection.XpYm || // Y is out of game field
                    start.x >= maxIndex && lineDirection == LineDirection.XpYm || // X is out of game field
                    start.x >= maxIndex && lineDirection == LineDirection.XpY0 || // X is out of game field
                    start.x >= maxIndex && lineDirection == LineDirection.XpYp || // X is out of game field
                    start.y >= maxIndex && lineDirection == LineDirection.XmYp || // Y is out of game field
                    start.y >= maxIndex && lineDirection == LineDirection.X0Yp || // Y is out of game field
                    start.y >= maxIndex && lineDirection == LineDirection.XpYp -> // Y is out of game field
                return Border
        }
        val nextX = start.x + lineDirection.dx
        val nextY = start.y + lineDirection.dy
        return Coordinates(nextX, nextY)
    }
}