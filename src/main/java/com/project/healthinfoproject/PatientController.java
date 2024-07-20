package com.project.healthinfoproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PatientController {

	static String dataRet = null;

	// Application UI can be viewed at http://localhost:8080/
	// Will take a long time to load unless the data from the API is cached
	// Might need to refresh the page

	@RequestMapping(value = "/data", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String getData() {

		// displays JSON of disease frequencies that gets passed to the frontend
		// this will take a long time to load unless it is already cached

		if (dataRet == null) {
			try {

				// Right now this request is only fetching 100 records of data. This can be
				// increased by changing _count=100 to a different number. Increasing this
				// willcause the application to load slower because it takes a while for the API
				// to execute the GET request.

				// This URL checks for:
				// Patient is deceased
				// Patient has a Condition of Body Mass Index 30+ - Obesity (based on SMOMED
				// code)
				// Patient has a Obeservation of Cause of Death (based on LOINC code)

				URL HAPI_API_CONDITION_BMI_OBESE_URL = new URL(
						"http://hapi.fhir.org/baseR4/Patient?deceased=true&_has:Condition:patient:code=162864005&_has:Observation:patient:code=69453-9&_revinclude=Observation:patient&_count=100&_format=json");

				System.out.println("Creating connection");
				URLConnection conn = HAPI_API_CONDITION_BMI_OBESE_URL.openConnection();

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

				Map<String, String> codeToDisplay = new HashMap<>();
				Map<String, Integer> codeToFrequency = new HashMap<>();

				JSONTokener tokener = new JSONTokener(br);
				JSONObject json = new JSONObject(tokener);

				// System.out.println(json);
				br.close();

				System.out.println("Tokenizing Entries");
				JSONArray entryArray = json.getJSONArray("entry");

				for (int i = 0; i < entryArray.length(); i++) {

					JSONObject resource = entryArray.getJSONObject(i).getJSONObject("resource");

					String resourceType = resource.getString("resourceType");

					if (resourceType.equals("Observation")) {

						if (resource.getJSONObject("code").getJSONArray("coding").getJSONObject(0).getString("code")
								.equals("69453-9")) {

							String code = resource.getJSONObject("valueCodeableConcept").getJSONArray("coding")
									.getJSONObject(0).getString("code");
							String display = resource.getJSONObject("valueCodeableConcept").getJSONArray("coding")
									.getJSONObject(0).getString("display");

							codeToDisplay.put(code, display);
							codeToFrequency.put(code, codeToFrequency.getOrDefault(code, 0) + 1);
						}
					}
				}

				JSONObject cdjson = new JSONObject(codeToDisplay);
				JSONObject cfjson = new JSONObject(codeToFrequency);
				JSONObject finaljson = new JSONObject();
				finaljson.put("codeToDisplay", cdjson);
				finaljson.put("codeToFrequency", cfjson);

				dataRet = finaljson.toString();
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}
		return dataRet;
	}

	@GetMapping("/generate-data")
	public String dataGenerator() {

		int numRecordsYouWantToCreate = 10;

		CreateData.createData(numRecordsYouWantToCreate);

		// Must go to http://localhost:8080/generate-data to execute this code.
		// HAPI API links to created data will appear in console;

		return "Data has been generated";
	}

	@GetMapping("/hello")
	public String hello() {

		return "Hello world";
	}
}
