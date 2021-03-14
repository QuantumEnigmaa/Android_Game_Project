package fr.isen.monsterfighter.Model

class Lobby {
    var lTurn:Int=0
    var lActiveStatus:Int=0
    var lPlayer1Id:String=""
    var lPlayer2Id:String=""

    constructor() {}

    constructor(lTurn:Int, lActiveStatus:Int, lPlayer1Id:String, lPlayer2Id:String) {
        this.lTurn = lTurn
        this.lPlayer2Id = lPlayer2Id
        this.lActiveStatus = lActiveStatus
        this.lPlayer1Id = lPlayer1Id
    }
}