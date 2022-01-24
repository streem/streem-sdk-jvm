package pro.streem.sdk

public class Streem private constructor(private val config: Config) {

    /**
     * Returns a [TokenBuilder] that can be used to create a Streem Token.
     *
     * This method is optimized for use from Java code. For Kotlin code, prefer the [buildToken] method.
     *
     * @param userId The user ID of the user to authenticate with this token.
     */
    public fun tokenBuilder(userId: String): TokenBuilder = TokenBuilder(config, userId)

    /**
     * Returns a string representation of a Streem Token configured with [builder].
     *
     * This method is optimized for use from Kotlin code. For Java code, prefer the [tokenBuilder] method.
     *
     * @param userId The user ID of the user to authenticate with this token.
     */
    public fun buildToken(userId: String, builder: StreemToken.() -> Unit): String =
        StreemToken(userId).also(builder).serialize(config)

    public companion object {
        /**
         * Returns a [Streem] instance for creating Streem Tokens with the provided Streem API Key ID and API Key
         * Secret.
         */
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
