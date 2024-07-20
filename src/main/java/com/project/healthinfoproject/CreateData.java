package com.project.healthinfoproject;

import java.util.ArrayList;
import java.util.Collections;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class CreateData {

	// Log the request
	static FhirContext ctx = FhirContext.forR4();

	// Create a client and post the transaction to the server
	static IGenericClient client = ctx.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

	public static void createData(int numRecords) {
		String[] codes = { "56265001", "230690007", "38341003", "73211009", "86049000", "39621005", "396275006" };

		String[] displays = { "Heart Disease", "Stroke", "High blood pressure", "Diabetes mellitus", "Cancer",
				"Gallbladder Disease", "Osteoarthritis" };

		int[] frequency = { 100, 50, 85, 90, 40, 70, 30 };

		ArrayList<Integer> bag = new ArrayList<>();
		for (int i = 0; i < frequency.length; i++) {

			for (int j = 0; j < frequency[i]; j++) {
				bag.add(i);
			}
		}
		Collections.shuffle(bag);
		for (int i = 0; i < numRecords; i++) {

			int randomDisease = bag.get((int) (Math.random() * (bag.size())));
			createDataHelper(codes[randomDisease], displays[randomDisease]);

			System.out.println(i + " : " + codes[randomDisease] + " " + displays[randomDisease]);
		}
	}

	public static void createDataHelper(String code, String display) {

		// Create a patient object
		Patient patient = new Patient();
		patient.addIdentifier().setSystem("http://acme.org/mrns").setValue("12345");
		patient.addName().setFamily("Jameson").addGiven("J").addGiven("Jonah");
		patient.setGender(Enumerations.AdministrativeGender.MALE);

		patient.setId(IdType.newRandomUuid());
		patient.setBirthDateElement(new DateType("2015-11-18"));
		patient.setDeceased(new BooleanType(true));

		MethodOutcome outcome = client.create().resource(patient).execute();

		IIdType id = outcome.getId();
		System.out.println("Created patient, got ID: " + id);

		Observation observation = new Observation();
		observation.setStatus(Observation.ObservationStatus.FINAL);
		observation.getCode().addCoding().setSystem("http://loinc.org").setCode("69453-9")
				.setDisplay("Cause of Death [US Standard Certificate of Death]");
		observation.setValue(new CodeableConcept().addCoding(new Coding("http://snomed.info/sct", code, display)));
		observation.setSubject(new Reference(id.toString().replaceAll("/_history/.*$", "")));

		MethodOutcome outcome2 = client.create().resource(observation).execute();
		IIdType id2 = outcome2.getId();
		System.out.println("Created observation, got ID: " + id2);

		Condition condition = new Condition(new Reference(id.toString().replaceAll("/_history/.*$", "")));

		condition.getCode()
				.addCoding(new Coding("http://snomed.info/sct", "162864005", "Body mass index 30+ - obesity"));

		condition.setClinicalStatus(new CodeableConcept().addCoding(
				new Coding().setCode("active").setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical")));
		condition.setVerificationStatus(new CodeableConcept().addCoding(new Coding().setCode("confirmed")
				.setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status")));

		MethodOutcome outcome3 = client.create().resource(condition).execute();
		IIdType id3 = outcome3.getId();

		System.out.println("Created condition, got ID: " + id3);
	}

}
