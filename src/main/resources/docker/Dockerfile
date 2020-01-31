FROM ubuntu:eoan

# Define version of GraalVM
ENV GRAAL_VERSION 19.3.1
ENV GRAAL_JAVA_VERSION 11
ENV GRAAL_FILENAME graalvm-ce-java${GRAAL_JAVA_VERSION}-linux-aarch64-${GRAAL_VERSION}.tar.gz
ENV MAVEN_VERSION 3.6.3
ENV JAVA_HOME /usr/lib/graalvm

# Install GraalVM
RUN apt-get update && \
    apt-get -y install gcc libc6-dev zlib1g-dev curl bash && \
    rm -rf var/lib/apt/lists/* && \
    curl -4 -L -k https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} -o /tmp/${GRAAL_FILENAME} $$ \
    tar -zxvf /tmp/${GRAAL_FILENAME} -C /tmp && \
    mv /tmp/graalvm-ce-java${GRAAL_JAVA_VERSION}-${GRAAL_VERSION} /usr/lib/graalvm $$ \
    rm -rf /tmp/*

# Install native-image 
RUN /usr/lib/graalvm/bin/gu install native-image

# Install Maven
RUN curl -4 -L -k https://www-us.apache.org/dist/maven/maven-3/${MAVEN_VERSION}}/binaries/apache-maven-${MAVEN_VERSION}}-bin.tar.gz -o /tmp/apache-maven-${MAVEN_VERSION} && \
    tar -zxvf /tmp/apache-maven-${MAVEN_VERSION} -C /tmp && \
    mv /tmp/apacha-maven-${MAVEN_VERSION} /usr/lib/maven && \
    rm -rf /tmp/*

# Create a volume to which we can mount to build:
VOLUME /target
WORKDIR /target

# And finally, run native-image
ENTRYPOINT ["/usr/lib/graalvm/bin/native-image"]
