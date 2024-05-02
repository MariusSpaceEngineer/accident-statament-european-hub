package com.inetum.realdolmen.hubkitbackend.utils;

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
import java.util.Arrays;
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

    public void sendStatement(File pdfFile, String... recipients) throws MailjetException, IOException {

        String encodedString = convertPDFFileToBase64(pdfFile);

        // Create a set of recipients to remove duplicates
        Set<String> recipientSet = new HashSet<>(Arrays.asList(recipients));

        // Create the TO field for the email
        JSONArray toField = new JSONArray();
        for (String recipient : recipientSet) {
            toField.put(new JSONObject().put("Email", recipient));
        }

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", senderEmail)
                                        .put("Name", senderName))
                                .put(Emailv31.Message.TO, toField)
                                .put(Emailv31.Message.SUBJECT, "Accident Statement:")
                                .put(Emailv31.Message.TEXTPART, "Dear user,\n\nPlease find attached the accident statement for your insurance. If you have any questions or need assistance, feel free to reach out to us.\n\nBest,\nThe CrashKit Team")
                                .put(Emailv31.Message.HTMLPART, "<p>Dear user,<br/>Please find attached the accident statement for your insurance. If you have any questions or need assistance, feel free to reach out to us.</p><p>Best,<br/>The CrashKit Team</p>")
                                .put(Emailv31.Message.ATTACHMENTS, new JSONArray()
                                        .put(new JSONObject()
                                                .put("ContentType", "application/pdf")
                                                .put("Filename", pdfFile.getName())
                                                .put("Base64Content", encodedString)))));
        MailjetResponse response = mailjetClient.post(request);
        System.out.println(response.getStatus());
        System.out.println(response.getData());

        CompletableFuture.completedFuture(null);
    }

    private static String convertPDFFileToBase64(File pdfFile) throws IOException {
        byte[] fileContent = Files.readAllBytes(pdfFile.toPath());
        String encodedString = Base64.getEncoder().encodeToString(fileContent);
        return encodedString;
    }
}
