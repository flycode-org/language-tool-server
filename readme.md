# LanguageTool Server

A JSON HTTP Server for [LanguageTool](https://languagetool.org/) with support for batching.

### Running in Docker

```
docker run -p 8080:8080 ghcr.io/flycode-org/langauge-tool-server
```

### Deployment

Deploy to app engine:

- Make sure to set the correct Google Cloud project before deploying

```
./gradlew appEngineDeploy
```
