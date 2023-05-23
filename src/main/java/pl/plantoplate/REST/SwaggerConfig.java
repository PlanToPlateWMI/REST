/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.

 */
package pl.plantoplate.REST;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

//http://localhost:8080/swagger-ui/index.html#/
@Configuration
public class SwaggerConfig {


    private static final String SCHEME_NAME = "bearerScheme";
    private static final String SCHEME = "Bearer";

    @Bean
    public OpenAPI customOpenAPI() {
        var openApi = new OpenAPI()
                .info(getInfo());

        addSecurity(openApi);

//        Server server = new Server();
//        server.setUrl("https://ambient-elf-336514.lm.r.appspot.com");
//        openApi.servers(List.of(server));

        return openApi;
    }

    private Info getInfo() {


        Contact contact = new Contact();
        contact.setEmail("plantoplatemobileapp@gmail.com");
        contact.setName("Plan To Plate Team");

        return new Info()
                .title("Your APIs Documentation")
                .description("The API documentation for your Portal.")
                .version("1.0.0")
                .contact(contact)
                .license(getLicense());
    }

    private License getLicense() {
        return new License()
                .name("Apache 2.0")
                .url("https://github.com/PlanToPlateWMI/REST/blob/main/LICENSE.md");
    }

    private void addSecurity(OpenAPI openApi) {
        var components = createComponents();
        var securityItem = new SecurityRequirement().addList(SCHEME_NAME);

        openApi
                .components(components)
                .addSecurityItem(securityItem);
    }

    private Components createComponents() {
        var components = new Components();
        components.addSecuritySchemes(SCHEME_NAME, createSecurityScheme());

        return components;
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME);
    }

//    public static final String AUTHORIZATION_HEADER = "Authorization";
//
//    private ApiKey apiKey(){
//        return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
//    }
//
//    private ApiInfo apiInfo(){
//        return new ApiInfo("Book API",
//                "REST API.\n" +
//                        "AuthController - User can log in and get JWT token or create an account",
//                "1.0.0",
//                "",
//                new Contact("Plan To Plate Teams","","plantoplatemobileapp@gmail.com"),
//                "Apache 2.0","https://github.com/PlanToPlateWMI/REST/blob/main/LICENSE.md", Collections.EMPTY_LIST);
//    }
//
//    @Bean
//    public Docket api(){
//        return new Docket(DocumentationType.SWAGGER_2)
//                .useDefaultResponseMessages(false)
//                .apiInfo(apiInfo())
//                .securityContexts(Arrays.asList(securityContext()))
//                .securitySchemes(Arrays.asList(apiKey()))
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("pl.plantoplate.REST.controller"))
//                .paths(paths())
//                .build();
//    }
//
//    private SecurityContext securityContext(){
//        return SecurityContext.builder().securityReferences(defaultAuth()).build();
//    }
//
//    private List<SecurityReference> defaultAuth(){
//        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
//        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
//        return Arrays.asList(new SecurityReference("JWT", authorizationScopes));
//    }
//
//
//    private Predicate<String> paths() {
//        return regex("/api/auth.*").or(regex("/invite-codes.*"));
//    }
}
