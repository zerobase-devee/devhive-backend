package com.devee.devhive.domain.auth.service.mail;

public interface MailService {

  void sendAuthEmail(String to) throws Exception;
}
