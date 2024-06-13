package geometry

/**
 * abstraction for any action to be done right after every move is made - it analyses the new state of the game field
 */
interface OneMoveProcessing {

    fun getMaxLengthAchievedForThisMove(where: Coordinates): Int?

    fun getCoordinatesFor(x: Int, y: Int, z: Int = 0): Coordinates
}