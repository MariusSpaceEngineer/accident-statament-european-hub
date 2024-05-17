package com.inetum.realdolmen.hubkitbackend.interfaces.services;

import com.inetum.realdolmen.hubkitbackend.models.AccidentStatement;
import com.mailjet.client.errors.MailjetException;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface IMailService {
    CompletableFuture<Void> sendWelcomeMail(String toEmail, String toFirstName) throws MailjetException;

    void sendStatement(File pdfFile, AccidentStatement accidentStatement) throws MailjetException, IOException;
}
