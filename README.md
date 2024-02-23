# Add Security and Authorization to a Java Spring Boot API

This repository contains a Spring Boot API starter project and demo project, both implementing API server application for menu items CRUD operations. The tutorial for creating this example is available on [Auth0 Developer Blog]().

**Prerequisites:**

> - [Java OpenJDK 17](https://jdk.java.net/java-se-ri/17)
> - [Auth0 account](https://auth0.com/signup)
> - [Auth0 CLI 1.0.0](https://github.com/auth0/auth0-cli#installation)

## Run the API demo project

To run the API demo project, which has the required dependencies for security configuration, execute the following commands:

```bash
git clone https://github.com/indiepopart/spring-menu-api.git
cd spring-menu-api/demo
```

## Register the API to Auth0

Sign up at [Auth0](https://auth0.com/signup) and install the [Auth0 CLI](https://github.com/auth0/auth0-cli). Then in the command line run:

```shell
auth0 login
```

The command output will display a device confirmation code and open a browser session to activate the device.

Register the API within your tenant:

```shell
auth0 apis create \
  --name "Menu API" \
  --identifier https://menu-api.okta.com \
  --scopes "create:items,update:items,delete:items" \
  --token-lifetime 86400 \
  --offline-access=false \
  --signing-alg "RS256"
```

The first line in the command output will contain your Auth0 domain.

## Run the Spring Boot API resource server

Create a copy of `.env.exmple`:

```shell
cp .env.example .env
```

Set the value of `OKTA_OAUTH2_ISSUER` with your Auth0 domain in `.env`:

```shell
OKTA_OAUTH2_ISSUER=https://<your-auth0-domain>/
```

Run the API with:

```shell
./gradlew bootRun
```

## Configure the WHATABYTE live client

For the Auth0 authentication, you need to register the live client as a Single-Page Application to Auth0:

```shell
auth0 apps create \
  --name "WHATABYTE Demo Client" \
  --description "Single-Page Application Dashboard for menu items CRUD" \
  --type spa \
  --callbacks https://dashboard.whatabyte.app/home \
  --logout-urls https://dashboard.whatabyte.app/home \
  --origins https://dashboard.whatabyte.app \
  --web-origins https://dashboard.whatabyte.app
```

The ClientID in the output will be required in the next step.

Go to the [WHATABYTE Dashboard](https://dashboard.whatabyte.app/home), and set _API Server Base URL_ to http://localhost:8080. Toggle on **Enable Authentication Features** and set the following values:

- Auth0 Domain: \<your-auth0-domain\>
- Auth0 Client ID: \<client-id\>
- Auth0 Callback URL: https://dashboard.whatabyte.app/home
- Auth0 API Audience: https://menu-api.okta.com

Enable RBAC. Set `menu-admin` in the _User Role_ text-box. Click on **Save**.


## Create and Assign Roles

Create some test users with the Auth0 CLI.


```shell
auth0 users create
```

Create the `menu-admin` role in the Auth0 tenant:

```shell
auth0 roles create
```

Assign the role to the user you created:

```shell
auth0 users roles assign
```

### Mapping the roles to token claims

The role `menu-admin` and its permissions must be mapped to a claim in the accessToken. [Configure your preferred editor](https://github.com/auth0/auth0-cli#customization) to use with the Auth0 CLI:

```shell
export EDITOR=nano
```

Create the Login Action:

```shell
auth0 actions create
```

Set the name __Add Roles__, and select **post-login** for the Trigger. When the editor opens, set the following implementation for the `onExecutePostLogin` function.

```javascript
exports.onExecutePostLogin = async (event, api) => {
  const namespace = 'https://menu-api.okta.com';
  if (event.authorization) {
    api.idToken.setCustomClaim('preferred_username', event.user.email);
    api.idToken.setCustomClaim(`${namespace}/roles`, event.authorization.roles);
    api.accessToken.setCustomClaim(`${namespace}/roles`, event.authorization.roles);
  }
}
```

Save the file. Then deploy the action:

```shell
auth0 actions deploy <ACTION_ID>
```

Attach the action to the login flow. You can do this with Auth0 [Management API for Actions](https://auth0.com/docs/api/management/v2#!/Actions/patch_bindings):

```shell
auth0 api patch "actions/triggers/post-login/bindings" \
  --data '{"bindings":[{"ref":{"type":"action_id","value":"<ACTION_ID>"},"display_name":"Add Roles"}]}'
```

Find the Menu API ID with:

```shell
auth0 apis list
```

Enable RBAC for the Menu API:

```shell
auth0 api patch "resource-servers/<API_ID>" \
  --data '{ "enforce_policies": true, "token_dialect": "access_token_authz" }'
```

Assign the permissions defined for the Menu API to the `menu-admin` role:

```shell
auth0 roles permissions add
```

Follow the instructions, and make sure to select all the API permissions:
- `create:items`
- `delete:items`
- `update:items`

All set, sign in  the WHATABYTE client with the user you created and you will be able to perform read and write operations over the menu items.

## Help

Please post any questions as comments on the [blog post](), or on the [Okta Developer Forums](https://devforum.okta.com/).

## License

Apache 2.0, see [LICENSE](LICENSE).
