FROM balenalib/rpi-raspbian:latest

RUN apt-get update && \
    apt-get -y install gcc libc6-dev zlib1g-dev curl bash && \
    rm -rf var/lib/apt/lists/*


# https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-19.3.1/graalvm-ce-java8-linux-amd64-19.3.1.tar.gz
# https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-19.3.1/graalvm-ce-java11-linux-amd64-19.3.1.tar.gz

# Define version of GraalVM
ENV GRAAL_VERSION 19.3.1
ENV GRAAL_JAVA_VERSION 11
ENV GRAAL_FILENAME graalvm-ce-java${GRAAL_JAVA_VERSION}-linux-aarch64-${GRAAL_VERSION}.tar.gz

# Download GraalVM
RUN curl -4 -L https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAAL_VERSION}/${GRAAL_FILENAME} -o /tmp/${GRAAL_FILENAME}

# Untar and move the files we need:
RUN tar -zxvf /tmp/${GRAAL_FILENAME} -C /tmp \
    && mv /tmp/graalvm-ce-java${GRAAL_JAVA_VERSION}-${GRAAL_VERSION} /usr/lib/graalvm

RUN rm -rf /tmp/*

RUN chmod +x /usr/lib/graalvm/bin/gu

RUN echo $USER

# RUN ls -la /usr/lib/graalvm/lib/installer/bin/
RUN /usr/lib/graalvm/bin/gu install native-image
# RUN ["/bin/bash", "/usr/lib/graalvm/bin/gu install native-image"]

# Create a volume to which we can mount to build:
VOLUME /target
WORKDIR /target

# And finally, run native-image
ENTRYPOINT ["/usr/lib/graalvm/bin/native-image"]
