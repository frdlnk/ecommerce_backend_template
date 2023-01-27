# Ecommerce Backend Template

This is a semi-complete ecommerce backend template in kotliin. Is Semi-Complete, because payment method is a thing very private, that cant be in a template

## Usage

- Clone the repository in a directory of your pc

```bash
git clone https://github.com/frdlnk/ecommerce_backend_template.git
```

- Open the project in your Java IDE (IntelliJ recommended to Kotlin support)

- Change this params:

```env
spring.data.mongodb.uri=yourdburi (with local db addres or atlas db address)
spring.data.mongodb.database=yourdatabase (db name)
```

```Dockerfile
FROM openjdk:17
COPY "./target/<yourcompiledfile>.jar" "app.jar"  (Change <yourcompiledfile for the name of the .jar file in target folder when app is compiled)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Contribution

If you want to contribute, open a pull request to change some code, create your branch and test all code there, then push it to master branch

## License

MIT