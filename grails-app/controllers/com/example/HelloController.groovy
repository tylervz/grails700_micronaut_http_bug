package com.example

import grails.converters.JSON
import groovy.transform.CompileStatic

@CompileStatic
class HelloController {

    def index() {
        render([message: "Hello world!"] as JSON)
    }
}
