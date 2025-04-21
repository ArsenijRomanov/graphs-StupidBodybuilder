package model

data class IntWeight(val value: Int): Weight<IntWeight>{
    override fun plus(other: IntWeight) = IntWeight(value + other.value)
    override fun minus(other: IntWeight) = IntWeight(value - other.value)
    override fun times(other: IntWeight) = IntWeight(value * other.value)
    override fun div(other: IntWeight) = IntWeight(value / other.value)

    override fun compareTo(other: IntWeight): Int = value.compareTo(other.value)

    override fun zero() = IntWeight(0)
    override fun isNegative() = (value < 0)
}

data class DoubleWeight(val value: Double) : Weight<DoubleWeight> {
    override fun plus(other: DoubleWeight) = DoubleWeight(value + other.value)
    override fun minus(other: DoubleWeight) = DoubleWeight(value - other.value)
    override fun times(other: DoubleWeight) = DoubleWeight(value * other.value)
    override fun div(other: DoubleWeight) = DoubleWeight(value / other.value)

    override fun compareTo(other: DoubleWeight): Int = value.compareTo(other.value)

    override fun zero() = DoubleWeight(0.0)
    override fun isNegative() = value < 0
}
