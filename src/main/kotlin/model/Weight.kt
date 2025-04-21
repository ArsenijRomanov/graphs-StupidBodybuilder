package model

interface Weight<T> : Comparable<T> {
    operator fun plus(other: T): T
    operator fun minus(other: T): T
    operator fun times(other: T): T
    operator fun div(other: T): T

    fun zero(): T
    fun infinity(): T
    fun isNegative(): Boolean
    fun min(first: T, second: T): T
    fun max(first: T, second: T): T
}
