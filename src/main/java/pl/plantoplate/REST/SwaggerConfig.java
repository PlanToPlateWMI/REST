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

/**
 * Swagger configuration
 */
//http://localhost:8080/swagger-ui/index.html#/
@Configuration
public class SwaggerConfig {


    /**
     * Scheme for JWT authorization SCHEME and information about API
     */
    private static final String SCHEME_NAME = "bearerScheme";
    private static final String SCHEME = "Bearer";

    /**
     * Creates Bean OpenAPI documentation with Security
     * @return
     */
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
                .title("PlanToPlate API")
                .description("API documentation")
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

}
