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

data class LongWeight(val value: Long): Weight<LongWeight>{
    override fun plus(other: LongWeight) = LongWeight(value + other.value)
    override fun minus(other: LongWeight) = LongWeight(value - other.value)
    override fun times(other: LongWeight) = LongWeight(value * other.value)
    override fun div(other: LongWeight) = LongWeight(value / other.value)

    override fun compareTo(other: LongWeight): Int = value.compareTo(other.value)

    override fun zero() = LongWeight(0)
    override fun isNegative() = (value < 0)
}

data class FloatWeight(val value: Float) : Weight<FloatWeight> {
    override fun plus(other: FloatWeight) = FloatWeight(value + other.value)
    override fun minus(other: FloatWeight) = FloatWeight(value - other.value)
    override fun times(other: FloatWeight) = FloatWeight(value * other.value)
    override fun div(other: FloatWeight) = FloatWeight(value / other.value)

    override fun compareTo(other: FloatWeight): Int = value.compareTo(other.value)

    override fun zero() = FloatWeight(0f)
    override fun isNegative() = value < 0
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
