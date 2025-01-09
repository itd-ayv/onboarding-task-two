package org.example

import groovy.json.JsonSlurper


class Main {
    static void main(String[] args) {
        def resourcePath = 'resource.json'
        println(resourcePath)
        def jsonFile = Main.class.getClassLoader().getResource(resourcePath)
        def jsonText = jsonFile.text

    def jsonSlurper = new JsonSlurper()
    def parsedData = jsonSlurper.parseText(jsonText)
        println(parsedData)
    }

}