The project is an OAuth2 Authorization Server serving "fully-leaded"
Cloud Foundry access tokens, i.e. with one of these tokens a client
app can push and modify other apps, on behalf of the authenticated
user. To use this service, log into it using your Cloud Foundry
credentials, and register a client (there's a basic UI for that). You
will get a client id and secret that you can use as regular OAuth2
client credentials with this server as an Authorization Server
(i.e. not the native Cloud Foundry Authorization Server). The service
is running at [https://cflogin.cfapps.io](https://cflogin.cfapps.io).

## How Can I Use It?

Once you have registered your client and got the credentials, you can
use it as a regular OAuth2 client with authorization code grants. For
example in a Spring Boot application, declare `@EnableOAuth2Sso` and
configure the client like this:

```yaml
security:
  oauth2:
    client:
      client-id: <your_client_id>
      client-secret: <your_client_secret>
      user-authorization-uri: https://cflogin.cfapps.io/oauth/authorize
      access-token-uri: https://cflogin.cfapps.io/oauth/token
    resource:
      user-info-uri: https://cflogin.cfapps.io/userinfo
```

The autoconfigured `OAuth2RestTemplate` (Spring Boot 1.3) or
`OAuth2ClientContext` (Spring Boot 1.4) will contain the access token
once a user has authenticated. You can use the `OAuth2RestTemplate` or
create your own to interact with the Cloud Foundry API (the cloud
controller). There is also a
[Java Client](https://github.com/cloudfoundry/cf-java-client) that
lets you inject the access token and gets you a higher level API for
managing the user's apps and spaces. See also the
[Cloud Foundry App Deployer](https://github.com/spring-cloud/spring-cloud-deployer-cloudfoundry)
from Spring Cloud as an even higher level abstraction.

## How Does it Work?

It works by impersonating the "cf" command line client, which means
there are a couple of downsides: 1) it has to collect the user's
credentials, and 2) the token scope cannot be controlled, and includes
not only `cloud_controller.*` (app management) but also
`password.write` (so the client app can change a user's password, if
it wants to).

The Cloud Foundry UAA (identity management) team are working on a
native solution that works better than this one, so they would like to
hear about your experience. If you are writing a client app, please
don't change user's passwords. If you are a user, and you want a safer
way for this stuff to work, send an e-mail to Ryan Morgan
(rmorgan@pivotal.io) and link back to this code.
