#source image
FROM ghcr.io/graalvm/native-image-community:24

# Set working directory
WORKDIR /app

# Install Maven
RUN microdnf install -y maven

# Copy the Maven POM and source code
COPY pom.xml .
COPY src ./src

# Build the native image (inside Docker)
RUN  mvn -Pnative native:compile

#Expose the application port
EXPOSE 9090

# Run the application
ENTRYPOINT ["/app/target/csvmaker"]
