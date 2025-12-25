# dragonball-quarkus-api

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./mvnw package
```


docker build -f .\docker\Dockerfile.jvm -t carlos89/dragonball-quarkus-api-jvm .
docker build -f .\docker\Dockerfile.native-micro -t carlos89/dragonball-quarkus-api-native .


