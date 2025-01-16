package org.example.service

import groovy.json.JsonBuilder
import groovy.sql.Sql
import de.itdesign.clarity.rest.ClarityRestClient
import de.itdesign.clarity.rest.RestResponse
import groovy.xml.XmlParser
import org.example.controller.ProjectController

import java.sql.Connection
import org.example.utils.dbUtil

class TeamService {
    static Connection connection = dbUtil.connect()
    static ProjectController projectController = new ProjectController()
    static void sendRequest(String xmlData) {
        Sql sql = new Sql(connection)
        ClarityRestClient rest = new ClarityRestClient("admin", sql.getConnection(), "http://10.0.0.173:7080")
        RestResponse response = null  // Declare response variable outside of try block

        try {
            def xmlParser = new XmlParser()
            def parsedXml = xmlParser.parseText(xmlData)
            parsedXml.'Projects'.'Project'.each { project ->
                def projectName = project.@name
                def projectId = project.@projectID
                // Retrieve the internal_id of the project from the database
                println(projectName)
                String internalId = projectController.getProjectInternalId(projectName as String)

                if (internalId) {
                    project.'Tasks'.'Task'.each { task ->
                        task.'Assignments'.'TaskLabor'.each { assignment ->
                            def resourceCode = assignment.@resourceID
                            // Retrieve resource details from the database
                            Map resourceDetails = projectController.getResourceDetails(resourceCode)

                            if (resourceDetails) {
                                def teamData = [
                                        resource: resourceDetails?.resource?.id
                                ]

                                print(teamData)

                                // Post the team assignment to the Clarity API
                               response = rest.POST("/projects/${internalId}/teams", teamData)

                                if (response?.jsonMap()) {
                                    println("Successfully added resource ${resourceDetails?.resource?.code} to project ${projectId} team.")
                                } else {
                                    // Handle the error if the resource is already assigned to another team (project)
                                    if (response?.jsonMap()?.errorCode == 'projmgr.TEAM_RESOURCE_ALREADY_STAFFED') {
                                        // Resource is already assigned to another project, check if it's the same one
                                        def existingProjectId = response?.jsonMap()?.errorMessage?.split(":")?.last()?.trim()
                                        if (existingProjectId != projectId) {
                                            // Resource is already assigned to another project, but not the current one
                                            println("Resource ${resourceDetails?.resource?.code} is already assigned to another project ${existingProjectId}, proceeding with adding to current project.")
                                        } else {
                                            println("Resource ${resourceDetails?.resource?.code} is already assigned to project ${projectId}. Skipping.")
                                        }
                                    } else {
                                        println("Failed to add resource ${resourceDetails?.resource?.code} to project ${projectId} team. Response: ${response?.jsonMap()}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            // The response is only accessible after the try block
            println "Response: ${response?.jsonMap()}"
        } catch (Exception e) {
            println "Caught exception: ${e.message}"
            e.printStackTrace()
        }
    }

    static RestResponse createTeam(String xmlData) {
        return sendRequest(xmlData)
    }

}
