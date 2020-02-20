docker run -it -v $(pwd)/:/target -e MAVEN_OPTS="-Xms400m -Xmx400m" --rm --entrypoint mvn alexanderhansen/graalvm-native package -P with-native-image
