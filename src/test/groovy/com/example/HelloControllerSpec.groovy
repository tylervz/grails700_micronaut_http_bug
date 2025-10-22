package com.example

import grails.testing.web.controllers.ControllerUnitTest
import org.springframework.http.HttpStatus
import spock.lang.Specification

class HelloControllerSpec extends Specification implements ControllerUnitTest<HelloController> {

    void "test index"() {
        when:
        request.method = "GET"
        controller.index()

        then:
        response.status == HttpStatus.OK.value()
        response.json != null
    }
}
