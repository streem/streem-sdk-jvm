# Streem SDK for Java & Kotlin

Server-side JVM library for interacting with the Streem API, and generation of Streem Tokens for use in client SDKs or Embedded SSO.

## Installation

Add the Streem SDK dependency to your project's `build.gradle` file:

```groovy
dependencies {
    implementation("pro.streem:streem-sdk-jvm:0.1.0")
}
```

## Usage

### Initialization

Initialize the library with your API Key ID and Secret:

_Kotlin_
```kotlin
val streem = Streem.getInstance(
    apiKeyId = yourApiKeyId,
    apiKeySecret = yourApiKeySecret,
    apiEnvironment = targetEnvironment, // optional, defaults to "prod-us"
)
```

_Java_
```java
var streem = Streem.getInstance(
    yourApiKeyId,
    yourApiKeySecret,
    targetEnvironment // optional, defaults to "prod-us"
);
```

### Streem Tokens

To create a Streem Token, call the `buildToken()` method (Kotlin) or create a `TokenBuilder` (Java) and then specify the details for the currently logged-in user:

_Kotlin_
```kotlin
val user = // an object that describes your logged-in user

// the user's ID is required to construct a token
val token = streem.buildToken(user.id) {
    // these fields are not required, but are recommended
    name = user.name
    email = user.email
    avatarUri = user.avatarUri

    // optional fields
    // Determines how long this token is valid for starting a session
    // (5 minutes is the default)
    tokenExpiration = Duration.ofMinutes(5))
    // Once the session has started, how long can the user remain logged in
    // (4 hours is the default) 
    sessionExpiration = Duration.ofHours(4)
}
```

_Java_
```java
var user = // an object that describes your logged-in user

// the user's ID is required to construct a token
var token = streem.buildToken(user.id)
        // these fields are not required, but are recommended
        .name(user.name)
        .email(user.email)
        .avatarUri(user.avatarUri)

        // optional fields
        // Determines how long this token is valid for starting a session
        // (5 minutes is the default)
        .tokenExpiration(Duration.ofMinutes(5)) 
        // Once the session has started, how long can the user remain logged in
        // (4 hours is the default) 
        .sessionExpiration(Duration.ofHours(4))

        // Finally, call `build()` to generate the token string
        .build();
```


#### Embedded SSO

Embedded SSO allows you to create Streem Tokens server-side, and automatically log your users into the Streem web application.

First, provide the `token` created above to your front-end browser client.  Next, place the token in the hash portion of any Streem web application URL, by appending `#token=...` with your token.

For example, to create an `iframe` to the root page in Streem, you might have:

```html
<iframe src="https://{company-code}.streempro.app#token={token}" />
```

Be sure to substitute `{company-code}` and `{token}` for the correct values.

#### Streem Client SDKs

If using the iOS or Android SDKs, you will provide the Streem Token to the client, and pass to the Streem SDK via `Streem.identify()` (iOS) or `Streem.login()` (Android).  More details can be found in the documentation of the individual SDKs.

## License

This repo is available as open source under the terms of the [MIT License](https://opensource.org/licenses/MIT).