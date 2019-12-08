package backend.logic.model

import backend.logic.enums.CoinCode
import java.math.BigDecimal

data class CoinWallet(val coinCode: CoinCode, val free: BigDecimal, val reserved: BigDecimal) {
    val all = free + reserved
    override fun toString(): String {
        return "CoinWallet(coinCode=$coinCode, free=$free, reserved=$reserved, all=$all)"
    }
}