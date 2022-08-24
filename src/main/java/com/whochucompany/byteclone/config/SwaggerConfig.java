package com.whochucompany.byteclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // RestController 에서 추가적인 어노테이션으로 상세 설명 설정 가능
//    @RestController
//    public class HelloController {
//
//        @Operation(summary = "test hello", description = "hello api example")
//        @ApiResponses({
//                @ApiResponse(responseCode = "200", description = "OK !!"),
//                @ApiResponse(responseCode = "400", description = "BAD REQUEST !!"),
//                @ApiResponse(responseCode = "404", description = "NOT FOUND !!"),
//                @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR !!")
//        })
//        @GetMapping("/hello")
//        public ResponseEntity<String> hello(@Parameter(description = "이름", required = true, example = "Park") @RequestParam String name) {
//            return ResponseEntity.ok("hello " + name);
//        }
//    }

    @Bean
    public Docket apiNews() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false) // swagger 기본 응답 변경 (200~, 400~, 500~), false 로 설정하면 기본 응답 코드 노출하지 않음
                .apiInfo(getApiInfo())// api 정보 , Swagger UI 로 노출할 정보
                .groupName("News")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.whochucompany.byteclone.controller")) // api 스펙에 작성되어 있는 패키지 (Controller) 를 지정
                .paths(PathSelectors.ant("/api/news/**")) // apis 에 있는 API 중 특정 path 를 선택
                .build();
    }

    @Bean
    public Docket apiMember() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false) // swagger 기본 응답 변경
                .apiInfo(getMemberApiInfo())// api 정보
                .groupName("Member")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.whochucompany.byteclone.controller"))
                .paths(PathSelectors.ant("/user/**"))
                .build();
    }

    private ApiInfo getMemberApiInfo() {
        return new ApiInfoBuilder()
                .title("Member API")
                .description("[BytePlus] Member API")
                .version("1.0")
                .build();
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title("News API")
                .description("[BytePlus] News API")
                .version("1.0")
                .build();
    }
}
