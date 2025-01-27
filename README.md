# Backend Serverless Integration Guide
- The yizhichan-client is an SDK which can support backend java application's serverless funcationality based on groovy code.
- Key features below:
1) Groovy Bean Real-time Load
2) Function Implant
3) Function Hotfix
## Below is the guide for how to integrate the SDK into your spring boot application

### Package Dependency
```xml
<!--SDK Coordinates-->
<dependency>
    <groupId>tech.yizhichan</groupId>
    <artifactId>serverless-sdk</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```
### Annotation Enable Switch
```java
import tech.yizhichan.client.annotation.EnableServerless;

@SpringBootApplication(scanBasePackages = ["xxx.xxx"])
@EnableServerless
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```
### Nacos Configuration
```yaml
# Add an extended configuration in the application's bootstrap.yml file
spring:
  cloud:
    nacos:
      config:
        extension-configs:
          - data-id: shared-serverless-config.${spring.cloud.nacos.config.file-extension:properties}
            group: ${spring.cloud.nacos.config.group}
            refresh: true
```
- The following are the configuration items for shared-serverless-config.properties:
```properties
# Whether to enable serverless
apaas.serverless.enable=true
# Tenant name
apaas.serverless.namespe=xxx
# Serverless bkend service address
openapi.client.app.serverless.host=https://xxx
# Serverless API version
openapi.client.app.serverless.version=api/v1
```

### 1. Bean Usage
#### 1.1 Create a new class in the management bkend
- particular instruction: For executing openfeign calls on the hosted service, you need to add the following annotation on the feignClient property injection to convert the feign call into a generic call. This annotation only serves as code translation and will be discarded during compilation.
```java
@org.springframework.beans.ftory.annotation.Resource
@tech.yizhichan.client.annotation.GenericFeignClientMapper(name = "xxxService", value = {
    @tech.yizhichan.client.annotation.GenericFeignClientMethodMapper(sourceMethodName = "test", httpMethod = "post", targetMethodUrl = "/xxx/xxx")
})
private ServiceFeignClient serviceFeignClient;
```

### 2. Function Usage
#### 2.1 Add the annotation @FunctionInjection to the method
- At runtime, the method's implementation will be dynamically replaced by script code.

#### 2.2 Annotation Parameter Details
- <span style="color:red;">name</span>: Function alias, mandatory parameter, must be unique under the same tenant. It should correspond one-to-one with the function name created in the console function management. It is recommended to use packagename.classname.methodname to ensure uniqueness.

- isAsync: Whether to execute asynchronously, default is false.

- traceId: Custom traceId, if not filled, it will be composed of namespace:appname:timestamp.

- clientIp: Current application's IP, can be empty.

#### 2.3 Function Script Specification
##### 2.3.1 The code logic must be wrapped in a class.
##### 2.3.2 Must include 5 reserved functions:

- accept: This function is used to define the preconditions for executing this function.

- onBefore: Pre-processing before the action method is executed.

- action: The actual function logic to be executed. To improve stability, the method execution timeout must be specified using the @TimedInterrupt annotation, in seconds. <span style="color:red;">The return type of action must be consistent with the original method's return type</span>.

- onException: Handling when the action method execution encounters an exception.

- onAfter: Post-processing after the action method is executed.Parameter list Map<String, Object> map:

##### 2.3.3 The framework will automatically merge the original method's parameter list into the map structure. For example, if the original method is:
```java
void print(int a, BigDecimal b, ApiResponse c)
```
the map will have keys a, b, c. The environment variables of the application under the tenant will also be loaded. Environment variables are set and managed in the console.
```java
import groovy.transform.TimedInterrupt

class GroovyFunctionTest {
    /**
     * Matching condition, if false, subsequent methods will not execute
     * @param map Request parameters, original method's parameter list merged into this map structure
     * @return
     */
    Boolean accept(Map<String, Object> map) {
        return true
    }

    /**
     * Pre-processing before the action method is executed
     * @param map Request parameters, original method's parameter list merged into this map structure
     */
    void onBefore(Map<String, Object> map) {
    }

    /**
     * The actual function logic to be executed
     * @param map Request parameters, original method's parameter list merged into this map structure. For example, if the original method is:
     * ApiResponse<String> testFunction(@RequestBody Request request);
     * @Data
     * public static class Request {
     *     private String key;
     *     private Integer num;
     * }
     * @return Must be consistent with the original method's return type
     */
    @TimedInterrupt(value = 60L)
    ApiResponse<String> action(Map<String, Object> map) {
        println("action")
        int num = (int) Optional.ofNullable(map.get('request').getAt('num')).orElse(3)
        while (num-- > 0) {
            println(Optional.ofNullable(map.get('request').getAt('key')).orElse("no key"))
        }
        String data = Optional.ofNullable(map.get('request').getAt('key')).orElse("!!") + ":updated"
        return ApiResponse.create("200", null, data)
    }

    /**
     * Handling when the action method execution encounters an exception
     * @param map Request parameters, original method's parameter list merged into this map structure
     */
    void onException(Map<String, Object> map) {
    }

    /**
     * Post-processing after the action method is executed
     * @param map Request parameters, original method's parameter list merged into this map structure
     */
    void onAfter(Map<String, Object> map) {
    }
}
```
#### 2.4 Version Management
Each time a function is saved in the console, a new record will be appended with the latest version number, and the old version will not be updated. By default, the latest version number (i.e., the largest version number) will be executed.

#### 2.5 Version Rollback
In the console, find the version you want to roll back to, click the enable button, and the SDK will execute the code of that version.


### 3. Hotfix Usage
#### 3.1 Create a hotfix task in the console
- <span style="color:red;">namespace</span>: Tenant name, mandatory.

- <span style="color:red;">appname</span>: Application name under the tenant, must match the ${spring.application.name} configuration item, mandatory.

- <span style="color:red;">classpath</span>: Class path to be fixed, mandatory.

- <span style="color:red;">methodName</span>: Class method name to be fixed, mandatory.

- <span style="color:red;">returnType</span>: Method return type class path, mandatory.

- <span style="color:red;">code</span>: Groovy code, mandatory.

- argNames: Parameter name array for the fix code.

- argTypes: Parameter type classpath array for the fix code.

- argValues: Parameter value array for the fix code.

- description: Description.

#### 3.2 Hotfix Script Specification
- The code logic must be wrapped in a class.

- The hotfix method must be named hotfix.

- <span style="color:red;">The return type of the hotfix method must be consistent with the original method's return type</span>.

- The parameters of the hotfix method can be different from the original method. If the parameters are specified in the console, they must be referenced in a Map<String, Object> structure, which will also include application-level environment variables.

- Code Example
##### Original Code
```java
@Service
public class TestService {
    public ResponseEntity<String> printForGroovyTest(String msg) {
        System.out.println("hello world");
        return ResponseEntity.ok(msg);
    }
}
```
##### Groovy Script for Hotfix
```java
import groovy.transform.TimedInterrupt

class GroovyHotfixTest {
    @TimedInterrupt(value = 10L)
    @groovy.transform.ThreadInterrupt
    org.springframework.http.ResponseEntity<String> hotfix() {
        println('Hotfix')
        return org.springframework.http.ResponseEntity.ok('Hotfix')
    }
}
```

#### 3.3 Publish
Click the publish button to publish the hotfix.
