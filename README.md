# cs48 s20 demo-spring-github-oauth-app

This code is intended to be a demo of a minimal web app using:
* Spring Boot as the backend
* Thymeleaf templating and server side rendering
* Only Bootstrap and JQuery as front-end frameworks
* Heroku Postgres as backend db
* Simple user management via Github OAuth
* Admin users defined in `application.properties` and overridable in `secrets-localhost.properties`
  and/or `secrets-heroku.properties`

Starter Code

# Configuration for OAuth

See instructions at <https://ucsb-cs48.github.io/topics/oauth_github_setup>

After configuring OAuth:
* Use `mvn spring-boot:run` to run on localhost

To run tests: `mvn test`

Note that for Heroku, you will need to login with the `heroku login` command line tool
in order to be able to run the script that setup up Heroku credentials.



