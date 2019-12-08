package backend.logic.model

import backend.logic.enums.CoinCode

data class TradeSymbol(val tradeCoin: CoinCode, val baseCoin: CoinCode) {
    override fun toString(): String {
        return tradeCoin.toString() + baseCoin.toString()
    }
}