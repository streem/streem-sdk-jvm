# Sample server for generating Streem Tokens

## Setup

First create your `.env` file:

```sh
cp .env.template .env
```

Then edit the `.env` file with your Streem-provided API Key ID and Secret. Add a randomly-generated `APP_SECRET` to prevent abuse of your test server if it's deployed publicly.

## Running the server

Start the server in one window:

```sh
./gradlew run
```

Then make a client request in another window:

```sh
curl --request POST --user "doesntmatter:$APP_SECRET" \
    --url http://localhost:8080/token \
    --header 'Accept: application/json' \
    --header 'Content-Type: application/json' \
    --data '{ "id": "12345", "name": "Test User", "email": "test.user@example.com" }'
```

