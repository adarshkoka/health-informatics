# Application Manual with Special Instructions

## Project Setup

1. Navigate to https://github.gatech.edu/akoka7/health-info-project
2. Clone or download the project (if downloaded then extract project).
3. Open Eclipse or any Java IDE.
4. Right-click in Project workspace, select "Import".
5. Select "Existing Maven Projects".
6. Put the directory of where the project was downloaded/cloned.
7. Wait for project to import, right-click project, select Maven -> Update Project.
8. Select the project and check "Force Update of Snapshots/Releases"

## Running the project

1. Right-click project, select Run As -> Spring Boot App.
2. After it finishes running, open a web browser.
3. Navigate to http://localhost:8080/. It will say "Please wait a few seconds". 
- NOTE: This will take a long time because the HAPI FHIR API takes a long time to make the GET request. I was not able to reduce this time because it is on the server side. The page does not need to be refresh and the application is ready once you see "Tokenizing Entries" in the IDE console. Once the data is loaded once, this page will load much faster because the data is being cached.
- If it takes more than 3 minutes then it's possible the HAPI FHIR API server is down (this happened to me once). You will receive a 504 Bad Gateway. This means that the application will need to be run later when the server is working which can be tested by going to this link: http://hapi.fhir.org/baseR4/Patient. You will get HTTP 200 OK if the server is working.
4. The JSON data with frequencies can be viewed at http://localhost:8080/data.
5. If the application does not contain any data (unlikely to happen) this means that the HAPI FHIR test server has reset their data.
- New data can be created on this server by navigating to  http://localhost:8080/generate-data.

## In the Application

1. Once the checkboxes have loaded in the application (http://localhost:8080/) check the boxes you want to see in the visualizations.
2. Click the button "Generate Visualizations!".
3. A bar chart and a pie chart will generate for the obesity related diseases you selected comparing the number of deaths in patients.