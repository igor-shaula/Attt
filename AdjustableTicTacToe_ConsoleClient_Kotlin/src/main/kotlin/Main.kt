fun main() {
    val field = AtttField(3, 2, 2)
    val rules = AtttRules(3)
    val game = AtttGame.create()
    game.prepare(field, rules)

    game.mm(1, 1)
    game.mm(0, 0)
    game.mm(1, 2)
    game.mm(1, 0)
    game.mm(2, 0)
    game.mm(0, 2)
    game.mm(0, 1)
    game.mm(2, 1)
    game.mm(2, 2)

    game.printCurrentFieldIn2d()

    println("engine.isActive() = " + game.isActive())
    game.finish()
    println("engine.isActive() = " + game.isActive())

    game.prepare(field, rules)
    game.prepare(AtttField(3), AtttRules(3))
    game.mm(1, 1) // X
    game.mm(2, 1) // O
    game.mm(2, 0) // X
    game.mm(0, 2) // O
    game.mm(1, 2) // X
    game.mm(2, 2) // O
    println("engine.isActive() = " + game.isActive())
    game.printCurrentFieldIn2d()
    game.mm(1, 0) // X - this one was problematic
    println("engine.isActive() = " + game.isActive())
}