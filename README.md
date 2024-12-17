# Spring Boot JTE Production Guide

This project demonstrates how to properly deploy a Spring Boot application using JTE (Java Template Engine) templates to production, including support for native images. It focuses on the key aspects of production deployment: precompiled templates and proper resource handling.

## Overview

When moving a Spring Boot + JTE application to production, there are two main deployment scenarios to consider:
- Building a traditional JAR with precompiled templates
- Creating a native image using GraalVM

Each scenario requires specific configuration and setup to ensure optimal performance and proper functionality.

## Development vs Production

### Development Mode

During development, JTE runs in development mode, which:
- Reloads templates on every request
- Provides detailed error messages
- Doesn't require precompilation

```properties
# application.properties
spring.application.name=jte-production
gg.jte.development-mode=true
```

### Production Mode

In production, JTE should:
- Use precompiled templates
- Disable development features
- Enable template caching

```properties
# application-prod.properties
gg.jte.developmentMode=false
gg.jte.usePrecompiledTemplates=true
logging.level.org.springframework.web=DEBUG
logging.level.gg.jte=DEBUG
```

## Building for Production

### Step 1: Precompiling Templates

Before building your production JAR, you need to precompile your JTE templates. This process:
- Converts templates to Java classes
- Improves runtime performance
- Reduces startup time
- Ensures template validity at build time

The precompiled templates will be generated in the `jte-classes` directory, as seen in this project's structure with `JteindexGenerated.java`.

### Step 2: Building the JAR

When building the JAR, ensure:
1. Templates are precompiled
2. Production properties are active
3. Precompiled classes are included in the build

```bash
# First, precompile templates
./mvnw compile

# Then build with production profile
./mvnw package -Pprod
```

## Native Image Support

### Understanding Resource Hints

When building a native image, GraalVM needs to know about resources that will be loaded at runtime. This project includes crucial runtime hints for JTE:

```java
@Component
public class ResourceRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Register .bin files needed by JTE
        hints.resources()
                .registerPattern("**/*.bin");

        // Register JTE generated classes for reflection
        hints.reflection()
                .registerType(JteindexGenerated.class, 
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, 
                    MemberCategory.INVOKE_DECLARED_METHODS);
    }
}
```

These hints are crucial because:
1. JTE uses `.bin` files for template metadata
2. Generated template classes need reflection access
3. Without these hints, the native image won't work correctly

### Building a Native Image

To build a native image:

1. Ensure GraalVM is installed and configured
2. Precompile templates
3. Register runtime hints (already done in this project)
4. Build the native image

```bash
# Precompile templates and build native image
./mvnw -Pnative native:compile
```

## Production Deployment Checklist

✅ Templates are precompiled
✅ Production properties are configured
✅ Runtime hints are registered
✅ Development mode is disabled
✅ Logging is properly configured
✅ Resource patterns are registered for native image

## Template Structure

The project includes a sample todo list template (`index.jte`):

```html
@param String pageTitle
@param java.util.List<dev.danvega.todo.Todo> todos

<!DOCTYPE html>
<html lang="en">
    <!-- Template content -->
</html>
```

This template will be precompiled to `JteindexGenerated.java` for production use.

## Troubleshooting

Common issues and solutions:

1. **Templates Not Found in Production**
    - Verify templates are precompiled
    - Check `gg.jte.usePrecompiledTemplates` is true
    - Ensure precompiled classes are included in the build

2. **Native Image Startup Errors**
    - Verify runtime hints are registered
    - Check for missing resource patterns
    - Ensure all required reflection configuration is present

3. **Performance Issues**
    - Confirm development mode is disabled
    - Verify templates are precompiled
    - Check logging levels in production

## Next Steps

Consider these production-ready enhancements:
- Add health check endpoints
- Implement metrics collection
- Configure production-grade logging
- Set up monitoring and alerting
- Implement caching strategies

This project provides a solid foundation for understanding how to properly deploy Spring Boot + JTE applications to production, whether as a traditional JAR or as a native image.