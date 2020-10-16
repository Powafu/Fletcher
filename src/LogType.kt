package scripts

enum class LogType(public val loglvl: IntRange, private val logTypeString: String, private val bowTypeString: String) {
    LOGS(0..10,"Logs", "Arrow shaft"),
    LOGS2(10..19,"Logs", "longbow"),
    OAK_LOGS(20..24,"Oak Logs", "Oak shortbow"),
    OAK_LOGS2(25..34,"Oak Logs", "Oak longbow"),
    WILLOW_LOGS(35..39,"Willow Logs", "Willow shortbow"),
    WILLOW_LOGS2(40..49,"Willow Logs", "Willow longbow"),
    MAPLE_LOGS(50..54,"Maple logs", "Maple shortbow"),
    MAPLE_LOGS2(55..69,"Maple logs","Maple longbow"),
    YEW_LOGS(70..84,"Yew Logs", "Yew longbow"),
    MAGIC_LOGS(85..99,"Magic Logs", "Magic longbow");

    fun logTypeString(): String {
        return logTypeString
    }

    override fun toString(): String {
        return logTypeString
    }

    companion object {
        private val map = LogType.values().associateBy(LogType::loglvl)
        fun fromInt(type: Int) = map[type]
    }
}