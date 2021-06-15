# Authentication

The purpose of the authentication is to ensure that the client issuing a call to the REST api is the legitimate owner
of the Polkadot account. The Polkadot signature is reused, which has several advantages:
* One unique authentication mechanism, regardless of whether it's an on- or off-chain operation.
* UX: No extra password, one password per Polkadot account
* Extra security: No password stored on backend.

Standard: [RFC 7519 - JSON Web Tokens](https://jwt.io/)

See this [sequence diagram](Authentication.puml) - requires PlanUML plugin for [Eclipse](https://plantuml.com/en/eclipse)
or [IntelliJ](https://plugins.jetbrains.com/plugin/7017-plantuml-integration) 

## Back-end
* Tokens are validated using spring-security filter.
* Operations (Http method `POST`) can still be protected by the existing signature mechanism
* Read access to resources (Http method `PUT` or `GET`) require authentication, 
    * Some resources can be statically protected via annotation `@PreAuthorize("hasRole('LEGAL_OFFICER')")`
    * Some resources require specific validations, for instance:
        * Fetching all protections requests of a requester is only possible by the requester him/herself.
        * Fetching all protections requests of a legal officer is only possible by the legal officer him/herself.
  

## Front-end
* Starts a session.
* Authenticates a session by signing a payload.
* Receives a token with a limited temporal validity.
* Add the token as an http header in each subsequent API call. 

## Open issues
* Where is the authentic source of legal officer address ?
* To Sign token: symmetric (secret) vs asymmetric (public/private key)
* We have no logion screen - what about UX ?