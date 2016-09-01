package org.lpw.tephra.util

import org.lpw.tephra.bean.ContextRefreshedListener
import org.lpw.tephra.scheduler.SecondsJob
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
  * @author lpw
  */
@Component("tephra.util.time-hash")
class TimeHashImpl extends TimeHash with SecondsJob with ContextRefreshedListener {
    @Value("${tephra.util.time-hash.range:0}")
    protected var range: Int = 0
    private val base: Int = 100000
    private var index: Int = 0
    private var codes: Array[Int] = new Array[Int](0)

    override def generate(): Int = {
        val time: Long = System.currentTimeMillis
        val n0: Long = time >> 10
        val n1: Long = (n0 % base) * (n0 / base)
        val n2: Long = time % 90 + 10
        val n3: Long = n2 * base + n1 % base
        var n4: Long = n3
        while (n4 >= 100) n4 = n4 >> 1

        (n3 * 100 + n4).toInt
    }

    override def isEnable: Boolean = range > 0

    override def valid(code: Int): Boolean = {
        if (range < 1) return true

        if (code < 100000000) return false

        var x: Int = code / 100
        while (x >= 100) x = x >> 1
        if (x != code % 100) return false

        x = (code / 100) % base
        for (n <- codes) if (x == n) return true

        false
    }

    override def executeSecondsJob(): Unit = {
        if (range > 0)
            generate(System.currentTimeMillis, range >> 1)
    }

    override def getContextRefreshedSort: Int = 9

    override def onContextRefreshed(): Unit = {
        if (range < 1) return

        codes = new Array[Int](range)
        val time = System.currentTimeMillis
        for (i <- -1 * (range >> 1) to (range >> 1))
            generate(time, i)
    }

    def generate(time: Long, offset: Int): Unit = {
        val t: Long = (time + offset * 1000) >> 10
        val n: Long = (t % base) * (t / base)
        codes(index) = (n % base).toInt
        index = (index + 1) % range
    }
}
