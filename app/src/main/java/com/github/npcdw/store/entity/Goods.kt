package com.github.npcdw.store.entity

import java.math.BigDecimal
import java.time.LocalDateTime

class Goods {
    var id: Int? = null
    var createTime: LocalDateTime? = null
    var updateTime: LocalDateTime? = null
    var qrcode: String? = null
    var name: String? = null
    var cover: String? = null
    var price: BigDecimal? = null
    var unit: String? = null
}