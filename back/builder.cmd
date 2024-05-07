@REM ./mvnw clean package -Pnative --define quarkus.native.container-build=true
./mvnw clean package

podman build -f .\src\main\docker\Dockerfile.jvm -t rgrback .