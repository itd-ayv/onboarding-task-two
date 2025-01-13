package org.example.service

import groovy.xml.MarkupBuilder
import groovy.json.JsonSlurper

class GenerateXml {

    def String generateResourceXml() {
        def jsonSlurper = new JsonSlurper()
        def jsonFile = getClass().getClassLoader().getResource(resourcePath)
        def jsonText = jsonFile.text
        def jsonData = jsonSlurper.parseText(jsonText)
        def writer = new StringWriter()
        def xmlBuilder = new MarkupBuilder(writer)
        xmlBuilder.NikuDataBus('xmlns:xsi': "http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation": "../xsd/nikuxog_resource.xsd") {
            Header(version: "6.0.12", action: "write", objectType: "resource", externalSource: "ORACLE-FINANCIAL")
            Resources {
                jsonData.resources.each { resource ->
                    Resource(resourceId: resource.resourceId, isActive: resource.isActive,
                            employmentType: resource.employmentType, hireDate: resource.hireDate,
                            terminationDate: resource.terminationDate, managerUserName: resource.managerUserName,
                            externalId: resource.externalId) {
                        PersonalInformation(lastName: resource.personalInformation.lastName,
                                firstName: resource.personalInformation.firstName,
                                emailAddress: resource.personalInformation.emailAddress)
                    }
                }
            }
        }
        new File('output.xml').text = writer.toString()
    }

}
