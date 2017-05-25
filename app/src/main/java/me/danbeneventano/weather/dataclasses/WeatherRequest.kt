package me.danbeneventano.weather.dataclasses

class WeatherRequest(val latitude: String,
                     val longitude: String,
                     val time: String? = null,
                     val units: Units? = null,
                     val language: Language? = null,
                     val excludeBlocksList: List<Block> = listOf(),
                     val extendBlocksList: List<Block> = listOf()) {

    private val excludeBlocks: String?
        get() = if (excludeBlocksList.isNotEmpty()) excludeBlocksList.joinToString(",") else null
    private val extendBlocks: String?
        get() = if (extendBlocksList.isNotEmpty()) extendBlocksList.joinToString(",") else null
    val queryParams: Map<String, String?>
        get() = mapOf(
                UNITS_KEY to units.toString(),
                LANGUAGE_KEY to language.toString(),
                EXCLUDE_KEY to excludeBlocks,
                EXTEND_KEY to extendBlocks
        )
    private val useTime: Boolean
        get() = time != null && time != ""
    private val params: String
        get() = "$latitude,$longitude"

    override fun toString(): String = if (useTime) "$params,$time" else params
}

const val UNITS_KEY = "units"

enum class Units(val value: String) {
    SI("si"),
    US("us"),
    CA("ca"),
    UK("uk"),
    AUTO("auto")
}

const val LANGUAGE_KEY = "lang"

enum class Language(val value: String) {
    ARABIC("ar"),
    BOSNIAN("bs"),
    CORNISH("kw"),
    CZECH("cs"),
    GERMAN("de"),
    GREEK("el"),
    HUNGARIAN("hu"),
    ICELANDIC("is"),
    NORWEGIAN("nb"),
    ENGLISH("en"),
    SPANISH("es"),
    FRENCH("fr"),
    CROATIAN("hr"),
    ITALIAN("it"),
    DUTCH("nl"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    RUSSIAN("ru"),
    SERBIAN("sr"),
    SLOVAK("sk"),
    SWEDISH("sv"),
    TETUM("tet"),
    TURKISH("tr"),
    UKRAINIAN("uk"),
    PIG_LATIN("x-pig-latin"),
    CHINESE("zh"),
    CHINESE_TRADITIONAL("zh-tw")
}

const val EXCLUDE_KEY = "exclude"
const val EXTEND_KEY = "extend"

enum class Block(val value: String) {
    CURRENTLY("currently"),
    MINUTELY("minutely"),
    HOURLY("hourly"),
    DAILY("daily"),
    ALERTS("alerts"),
    FLAGS("flags")
}