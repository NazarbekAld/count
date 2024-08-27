package cloud.rout.data

import kotlinx.serialization.Serializable

@Serializable
data class CountData(val theCount: Int) {
    infix fun plus(other: CountData): CountData {
        return CountData(theCount + other.theCount)
    }

    operator fun inc(): CountData {
        return CountData(theCount + 1)
    }

}