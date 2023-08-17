package com.devee.devhive.auth.service.mail;

public interface MailService {

  void sendAuthEmail(String to) throws Exception;
}
