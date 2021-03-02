package fr.isen.monsterfighter.Model

class Lobby {
    var lTurn:Int=0
    var lActiveStatus:Int=0
    var lPlayer1Id:Int=0
    var lPlayer2Id:Int=0

    constructor() {}

    constructor(lTurn:Int, lActiveStatus:Int, lPlayer1Id:Int, lPlayer2Id:Int) {
        this.lTurn = lTurn
        this.lPlayer2Id = lPlayer2Id
        this.lActiveStatus = lActiveStatus
        this.lPlayer1Id = lPlayer1Id
    }
}