class ApiError {
    val status_code: Int = 0
    private val message: String? = null

    fun status(): Int {
        return status_code
    }

    fun message(): String {
        return message!!
    }
}