package view;

import controller.service.api.IBankService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Class which creates transaction check for printing on console and saving in file */
public class CheckView {

    /** Instance of service for retrieving bank name from database */
    private final IBankService bankService;

    /** Template for transaction checks */
    private final String checkTemplate = """
                                   ----------------------------------------
                                   |            Банковский чек            |
                                   | Чек:%s%s |
                                   | %s%s%s |
                                   | Тип транзакции:%s%s |
                                   | Банк отправителя:%s%s |
                                   | Банк получателя:%s%s |
                                   | Счёт отправителя:%s%d |
                                   | Счёт получателя:%s%d |
                                   | Сумма:%s%s BYN |
                                   |--------------------------------------|
                                   """;

    /** Formatter for transaction date */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Formatter for transaction time */
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** Constructor with parameter for accessing bank information */
    public CheckView(IBankService service) {
        this.bankService = service;
    }

    /**
     * Fills all aligned fields in template with provided data
     *
     * @param id transaction id
     * @param date transaction date
     * @param time transaction time
     * @param senderBankId id of bank which owns sender account
     * @param receiverBankId id of bank which owns receiver account
     * @param senderId sender account id
     * @param receiverId receiver account id
     * @param amount transaction amount
     *
     * @return String with complete check
     */
    public String getCheck(long id, LocalDate date, LocalTime time, long senderBankId, long receiverBankId,
                           long senderId, long receiverId, double amount) {
        String checkDate = dateFormatter.format(date);
        String checkTime = timeFormatter.format(time);
        String transactionType = getOperationType(senderId, receiverId);
        String senderBank = senderBankId != 0 ? bankService.getBank(senderBankId).getName() : "";
        String receiverBank = receiverBankId != 0 ? bankService.getBank(receiverBankId).getName() : "";
        String amountStringValue = String.format("%.2f", amount);

        return checkTemplate.formatted(" ".repeat(32 - String.valueOf(id).length()), id,
                checkDate, " ".repeat(18), checkTime,
                " ".repeat(21 - transactionType.length()), transactionType,
                " ".repeat(19 - senderBank.length()), senderBank,
                " ".repeat(20 - receiverBank.length()), receiverBank,
                " ".repeat(19 - String.valueOf(senderId).length()), senderId,
                " ".repeat(20 - String.valueOf(receiverId).length()), receiverId,
                " ".repeat(26 - String.valueOf(amountStringValue).length()), amountStringValue);
    }

    /**
     * Determines transaction type based on sender and receiver id
     *
     * @param senderId sender account id
     * @param receiverId receiver account id
     *
     * @return transaction type
     */
    private String getOperationType(long senderId, long receiverId) {
        if (senderId != 0 && receiverId != 0) {
            return "Перевод";
        } else if (senderId == 0 && receiverId != 0) {
            return "Пополнение счёта";
        } else if (senderId != 0) {
            return "Снятие средств";
        }

        return "Неизвестная операция";
    }
}