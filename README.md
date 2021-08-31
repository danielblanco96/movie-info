# Movie Info

This is the repository for the Salesforce-on-Slack Movie Info Coding Take-home Assignment.

## Prerequisites
* Have Java and Maven installed.
* Ngrok exposing the port number 3000 of your local machine.
* Slack account and workspace with home tab configured [with this JSON](https://github.com/danielblanco96/movie-info/blob/main/assets/view/home.json), changing the user_id field.

## Environment
Before running the slack app, set the next environment variables in your system.
```
export SLACK_BOT_TOKEN=<YOUR_BOT_TOKEN>
export SLACK_SIGNING_SECRET=<YOUR_SIGNING_SECRET>
export THEMOVIEDB_API_KEY=<YOUR_API_KEY>
```

## Run the app
To run the slack app execute the following commands from the repository root.
```
cd spring-boot-app
mvn spring-boot:run
```
It will download the last movie export from [themoviedb](https://www.themoviedb.org/) and build the internal dictionary. After finishing this process, you will see a message with the number of movies imported (over 600,000).
