package org.example.service

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.sql.Sql
import de.itdesign.clarity.rest.ClarityRestClient
import de.itdesign.clarity.rest.RestResponse
import java.sql.Connection
import org.example.utils.dbUtil
import de.itdesign.clarity.logging.CommonLogger

class ProjectService {
    static Sql sql
    static CommonLogger cmnLog = new CommonLogger(this)

    static Connection connection = dbUtil.connect()

    static RestResponse sendRequest(String httpMethod, String endpoint, Map data = null) {
        Sql sql = new Sql(connection)
        ClarityRestClient rest = new ClarityRestClient("admin", sql.getConnection(), cmnLog, "http://10.0.0.173:7080")
        def jsonData = data ? new JsonBuilder(data).toString() : null
        RestResponse response
        try {
            if (httpMethod == 'POST') {
                response = rest.POST(endpoint, jsonData)
                println("hello")
                cmnLog.info("hey")
            } else if (httpMethod == 'PATCH') {
                response = rest.PATCH(endpoint, jsonData)
            } else if (httpMethod == 'GET') {
                response = rest.GET(endpoint)
            }
            //  println "Response: ${response?.jsonMap()}"
        } catch (Exception e) {
            println "Caught exception: ${e.message}"
            e.printStackTrace()
        } finally {
            rest?.close()
        }

        return response
    }

    static RestResponse createProject(Map projectData) {
        return sendRequest('POST', '/projects', projectData)
    }

    static RestResponse updateProject(String projectId, Map projectData) {
        return sendRequest('PATCH', "/projects/${projectId}", projectData)
    }

    static RestResponse getAllProject(String projectId) {
        return sendRequest('GET', "/projects/${projectId}")
    }

    static List<Map> getProject(List<Map> projectNamesFromJson) {
        def allProjects = []
        println(projectNamesFromJson)

        def projectNames = projectNamesFromJson.collect { it.name }
        println(projectNames)
        def statement = connection.createStatement()
        def resultSet = statement.executeQuery("SELECT ID, CODE, NAME FROM INV_INVESTMENTS WHERE name IN (${projectNames.collect { "'${it}'" }.join(",")})")
        while (resultSet.next()) {
            def project = [
                    id  : resultSet.getInt("ID"),   // Get the ID from the result set
                    code: resultSet.getString("CODE"), // Get the CODE from the result set
                    name: resultSet.getString("NAME")  // Get the NAME from the result set
            ]

            // Append the project data to the allProjects list
            allProjects << project
        }
        return allProjects
    }

    // Method to retrieve project internal_id from the database using project name
    static String getProjectInternalId(String projectName) {
        def statement = connection.createStatement()
        def query = "SELECT ID, CODE, NAME FROM INV_INVESTMENTS WHERE name = ?"
        def preparedStatement = connection.prepareStatement(query)

        preparedStatement.setString(1, projectName)
        def resultSet = preparedStatement.executeQuery()
        // If the query returns a result, retrieve the ID
        if (resultSet.next()) {
            println(resultSet.getString("ID"))
            return resultSet.getString("ID")
        } else {
            return null  // If no matching project is found, return null
        }
    }
}
