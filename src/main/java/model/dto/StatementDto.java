package model.dto;

import java.time.LocalDateTime;

/** An object for retrieving information from database for user transaction statement */
public record StatementDto(LocalDateTime time, String sender, String receiver, double amount) { }