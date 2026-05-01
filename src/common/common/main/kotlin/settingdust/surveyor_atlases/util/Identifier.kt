package settingdust.surveyor_atlases.util

data class Identifier(
    val namespace: String = DEFAULT_NAMESPACE,
    val path: String,
) {
    constructor(path: String) : this(DEFAULT_NAMESPACE, path)

    override fun toString(): String = "$namespace:$path"

    companion object {
        val DEFAULT_NAMESPACE = "minecraft"
        fun parse(value: String): Identifier {
            val separator = value.indexOf(':')
            return if (separator == -1) {
                Identifier("minecraft", value)
            } else {
                Identifier(value.substring(0, separator), value.substring(separator + 1))
            }
        }
    }
}
