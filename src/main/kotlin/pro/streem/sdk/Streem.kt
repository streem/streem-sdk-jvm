package pro.streem.sdk

public class Streem private constructor(
    private val config: Config
) {
    public fun tokenBuilder(userId: String): TokenBuilder = TokenBuilder(config, userId)

    public fun buildToken(userId: String, block: TokenBuilder.() -> Unit): String =
        tokenBuilder(userId).also(block).build()

    public companion object {
        @JvmStatic
        @JvmOverloads
        @Throws(IllegalArgumentException::class)
        public fun getInstance(
            apiKeyId: String,
            apiKeySecret: String,
            apiEnvironment: String = "prod-us"
        ): Streem = Streem(Config(apiKeyId, apiKeySecret, apiEnvironment))
    }
}
