package org.example

import org.example.service.GenerateXml
import org.example.service.ReadJsonProject
import org.example.service.ReadJsonTask


class Main {
    static void main(String[] args) {
        ReadJsonProject readJsonProject = new ReadJsonProject()
        //readJsonProject.readAndCreateProjects()
        ReadJsonTask readJsonTask = new ReadJsonTask()
        readJsonTask.readAndCreateTasks()
        GenerateXml generateResourceXml = new GenerateXml()
        //generateResourceXml.generateResourceXml()

    }
}