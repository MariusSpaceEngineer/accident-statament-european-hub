package com.inetum.realdolmen.hubkitbackend.utils;

import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;
import com.inetum.realdolmen.hubkitbackend.models.Driver;
import com.inetum.realdolmen.hubkitbackend.models.InsuranceCertificate;
import com.inetum.realdolmen.hubkitbackend.models.PolicyHolder;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class MailService {

    @Autowired
    private MailjetClient mailjetClient;
    @Value("${mailjet.sender.name}")
    private String senderName;
    @Value("${mailjet.sender.email}")
    private String senderEmail;


    @Async
    public CompletableFuture<Void> sendWelcomeMail(String toEmail, String toFirstName) throws MailjetException {
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", senderEmail)
                                        .put("Name", senderName))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", toEmail)
                                                .put("Name", toFirstName)))
                                .put(Emailv31.Message.SUBJECT, "Welcome to CrashKit, " + toFirstName + "!")
                                .put(Emailv31.Message.TEXTPART, "Dear " + toFirstName + ",\n\nCongratulations on successfully registering at CrashKit! We're excited to have you on board. If you have any questions or need assistance, feel free to reach out to us.\n\nBest,\nThe CrashKit Team")
                                .put(Emailv31.Message.HTMLPART, "<p>Dear " + toFirstName + ",<br/>Congratulations on successfully registering at CrashKit! We're excited to have you on board. If you have any questions or need assistance, feel free to reach out to us.</p><p>Best,<br/>The CrashKit Team</p>")));
        MailjetResponse response = mailjetClient.post(request);
        System.out.println(response.getStatus());
        System.out.println(response.getData());

        return CompletableFuture.completedFuture(null);
    }

    public void sendStatement(File pdfFile, AccidentStatement accidentStatement) throws MailjetException, IOException {
        String encodedString = convertPDFFileToBase64(pdfFile);

        // Create a set to store insurance agency emails to avoid duplicates
        Set<String> insuranceEmails = new HashSet<>();

        // Send emails to policyholders, insurance agencies, and drivers
        for (int i = 0; i < accidentStatement.getPolicyHolders().size(); i++) {
            PolicyHolder policyHolder = accidentStatement.getPolicyHolders().get(i);
            Driver driver = accidentStatement.getDrivers().get(i);

            // Send email to policyholder
            sendEmailWithPDFAttachment(policyHolder.getEmail(), "An accident statement has been created on your name",
                    "Dear " + policyHolder.getFirstName() + " " + policyHolder.getLastName() + ",\n\nPlease find attached the accident statement for your insurance. If you have any issues or this wasn't intended, please inform your insurance agency.\n\nBest,\nThe CrashKit Team",
                    "<p>Dear " + policyHolder.getFirstName() + " " + policyHolder.getLastName() + ",<br/>Please find attached the accident statement for your insurance. If you have any issues or this wasn't intended, please inform your insurance agency.</p><p>Best,<br/>The CrashKit Team</p>",
                    encodedString);

            // Send email to insurance agencies
            for (InsuranceCertificate insuranceCertificate : policyHolder.getInsuranceCertificates()) {
                if (!insuranceEmails.contains(insuranceCertificate.getInsuranceAgency().getEmail())) {
                    sendEmailWithPDFAttachment(insuranceCertificate.getInsuranceAgency().getEmail(), "One of your clients has had an accident",
                            "Dear Agency,\n\nAn accident statement has been made for one of your clients with the name: " + policyHolder.getFirstName() + " " + policyHolder.getLastName() + ". Please find the details in the attached document.\n\nBest,\nThe CrashKit Team",
                            "<p>Dear Agency,<br/>An accident statement has been made for one of your clients with the name: " + policyHolder.getFirstName() + " " + policyHolder.getLastName() + ". Please find the details in the attached document.</p><p>Best,<br/>The CrashKit Team</p>",
                            encodedString);
                    insuranceEmails.add(insuranceCertificate.getInsuranceAgency().getEmail());
                }
            }

            // Send email to driver if the driver's email is not the same as the policyholder's email
            if (!driver.getEmail().equals(policyHolder.getEmail())) {
                sendEmailWithPDFAttachment(driver.getEmail(), "An accident statement has been created on your name",
                        "Dear " + driver.getFirstName() + " " + driver.getLastName() + ",\n\nHere is a copy of the accident statement to which you were part of but were not the policy holder. If there are any discrepancies, please inform your insurance agency.\n\nBest,\nThe CrashKit Team",
                        "<p>Dear " + driver.getFirstName() + " " + driver.getLastName() + ",<br/>Here is a copy of the accident statement to which you were part of but were not the policy holder. If there are any discrepancies, please inform your insurance agency.</p><p>Best,<br/>The CrashKit Team</p>",
                        encodedString);
            }

            // Delete the PDF file
            if (!pdfFile.delete()) {
                System.out.println("Failed to delete the file: " + pdfFile.getAbsolutePath());
            }
        }
    }

    private void sendEmailWithPDFAttachment(String recipientEmail, String subject, String textPart, String htmlPart, String encodedString) throws MailjetException, IOException {
        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", senderEmail)
                                        .put("Name", senderName))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject().put("Email", recipientEmail)))
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.TEXTPART, textPart)
                                .put(Emailv31.Message.HTMLPART, htmlPart)
                                .put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                                        .put(new JSONObject()
                                                .put("ContentType", "application/pdf")
                                                .put("Filename", "Accident Statement") //TODO add the right name
                                                .put("Base64Content", encodedString)))));
        MailjetResponse response = mailjetClient.post(request);
        System.out.println(response.getStatus());
        System.out.println(response.getData());
    }


    private static String convertPDFFileToBase64(File pdfFile) throws IOException {
        byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
