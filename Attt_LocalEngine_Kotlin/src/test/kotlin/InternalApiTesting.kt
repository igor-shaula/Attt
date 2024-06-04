import internalElements.AtttEngine
import internalElements.AtttPlayerImpl
import internalElements.Coordinates
import utilities.Log
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse

class InternalApiTesting {

    @BeforeTest
    fun switchLoggingOn() {
        Log.switch(true)
    }

    // this test was provided by Matt Tucker - https://github.com/tuck182 - many thanks for finding a serious bug!
    @Test
    fun test3x3FieldWithMultiplePossibleLines() {
        AtttEngine.prepareGame(3, 3)

        // .Xx
        // .xo
        // oxo

        AtttEngine.makeMove(Coordinates(1, 1), AtttPlayerImpl.A)
        AtttEngine.makeMove(Coordinates(2, 1), AtttPlayerImpl.B)
        AtttEngine.makeMove(Coordinates(2, 0), AtttPlayerImpl.A)
        AtttEngine.makeMove(Coordinates(0, 2), AtttPlayerImpl.B)
        AtttEngine.makeMove(Coordinates(1, 2), AtttPlayerImpl.A)
        AtttEngine.makeMove(Coordinates(2, 2), AtttPlayerImpl.B)
        AtttEngine.makeMove(Coordinates(1, 0), AtttPlayerImpl.A)

        assertFalse(AtttEngine.isActive(), "Game should have been won")
        // Would be nice to be able to do this:
        // assertEquals(AtttPlayer.A, internalElements.AtttEngine.getWinner())
    }

    @Test
    fun having3x3Field_realSimulation2PlayersMovesMade_victoryConditionIsCorrect() {
        prepareClassic3x3GameField()
        AtttEngine.makeMove(Coordinates(0, 0))
        AtttEngine.makeMove(Coordinates(1, 0))
        AtttEngine.makeMove(Coordinates(2, 0))
        AtttEngine.makeMove(Coordinates(1, 1))
        AtttEngine.makeMove(Coordinates(2, 1))
        AtttEngine.makeMove(Coordinates(1, 2))
        // gameField & winning message for player B is printed in the console
        // todo: add assertion here
    }

    @Test
    fun having3x3Field_realSimulation2PlayersShortenedMovesMade_victoryConditionIsCorrect() {
        prepareClassic3x3GameField()
        AtttEngine.makeMove(0, 0)
        AtttEngine.makeMove(1, 0)
        AtttEngine.makeMove(2, 0)
        AtttEngine.makeMove(1, 1)
        AtttEngine.makeMove(2, 1)
        AtttEngine.makeMove(1, 2)
        // gameField & winning message for player B is printed in the console
        // todo: add assertion here
    }

    @Test
    fun test5x5Field() {
        val gameField = AtttField(5)
        AtttEngine.prepareGame(5, 5)
        Log.pl("\ntest3x3Field: gameEngine ready with given field: ${gameField.prepareForPrintingIn2d()}")
        AtttEngine.makeMove(Coordinates(0, 0), AtttPlayerImpl.A)
        AtttEngine.makeMove(Coordinates(1, 0), AtttPlayerImpl.A)
//    GameEngine.makeNewMove(Coordinates(2, 0), WhichPlayer.A) // intentionally commented - it will be used a bit later
        AtttEngine.makeMove(Coordinates(3, 0), AtttPlayerImpl.A)
        AtttEngine.makeMove(Coordinates(4, 0), AtttPlayerImpl.A)
        AtttEngine.makeMove(Coordinates(2, 0), AtttPlayerImpl.A) // intentionally placed here to connect 2 segments
        // todo: add assertion here
    }
}