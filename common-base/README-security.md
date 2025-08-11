## ❓ Q1: What is the purpose of `AuthenticationEntryPoint`?
🅰️: `AuthenticationEntryPoint` is used to handle unauthorized access **when no authentication is provided**. It is triggered when a request is made to a protected resource without valid credentials (e.g., missing or expired JWT). It typically returns a `401 Unauthorized` response.

## ❓ Q2: When is `AuthenticationEntryPoint` called?
🅰️: It is called **before any authentication attempt**, when a user tries to access a secured endpoint **without being authenticated**.

## ❓ Q3: What is the purpose of `AuthenticationFailureHandler`?
🅰️: `AuthenticationFailureHandler` is triggered **when authentication fails** during a login attempt. For example, if a user provides invalid credentials (wrong username or password), this handler is called to return an error response.

## ❓ Q4: When is `AuthenticationFailureHandler` called?
🅰️: It is called `during an authentication attempt`, typically through a login form or custom login endpoint, `when the provided credentials are invalid`.

## ❓ Q5: How are they configured in Spring Security?
🅰️: `AuthenticationEntryPoint` is configured like this:
````java
http.exceptionHandling()
    .authenticationEntryPoint(unauthorizedHandler);
````

`AuthenticationFailureHandler` is set on the login filter:
````java
customLoginFilter.setAuthenticationFailureHandler(customFailureHandler);
````

##  Q6: What is a simple analogy to understand the difference?
- `AuthenticationEntryPoint`: <br/>
  You tried to enter a restricted area without showing any ID — you get stopped at the gate.
- `AuthenticationFailureHandler`: <br/>
  You showed an ID, but it was fake or invalid — you get rejected during verification.


## Summary Table?
| Feature                | `AuthenticationEntryPoint`                         | `AuthenticationFailureHandler`       |
| ---------------------- | -------------------------------------------------- |--------------------------------------|
| **When triggered**     | No authentication provided                         | Invalid authentication attempt       |
| **Typical context**    | Accessing a protected resource without login/token | Login attempt with wrong credentials |
| **Returns**            | 401 Unauthorized                                   | 401, error message, or redirect      |
| **Use case**           | Stateless APIs (e.g. JWT)                          | Login flow  (Form login / custom login filters)                       |
| **Where to configure** | `http.exceptionHandling()`                         | On login/authentication filter       |
