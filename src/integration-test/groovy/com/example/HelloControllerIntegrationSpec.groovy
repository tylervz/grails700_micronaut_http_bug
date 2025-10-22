package com.example

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import grails.testing.spock.OnceBefore
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import spock.lang.Shared
import spock.lang.Specification

@Integration
@Rollback
class HelloControllerIntegrationSpec extends Specification {

    @Shared
    HttpClient client

    @OnceBefore
    void init() { 
        // serverPort is automatically injected
        String baseUrl = "http://localhost:$serverPort"
        this.client  = HttpClient.create(baseUrl.toURL())
    }

    void "test index"() {
        when:
        HttpRequest request = HttpRequest.GET("/Hello")
        HttpResponse<String> response = this.client.toBlocking().exchange(request, String)

        then:
        response.status == HttpStatus.OK
        response.body() != null
        response.body() == '{"message":"Hello world!"}'
    }
}
