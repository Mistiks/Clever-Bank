package view;

import controller.service.api.IBankService;
import controller.service.api.IUserService;
import model.dto.StatementDto;
import model.entity.Account;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Class which creates transaction statement for printing on console and saving in file */
public class TransactionStatementView {

    /** Instance of service for retrieving bank name from database */
    private final IBankService bankService;

    /** Instance of service for retrieving bank name from database */
    private final IUserService userService;

    /** Output formatter for dates in transaction statement */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Output formatter for time of transaction statement formation */
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /** Template for statement header */
    private String statementTemplate = """
                                           Выписка
            %s%s
            Клиент                        | %s
            Счёт                          | %s
            Валюта                        | BYN
            Дата открытия                 | %s
            Период                        | %s — %s
            Дата и время формирования     | %s, %s
            Остаток                       | %s BYN
               Дата     |         Примечание                    | Сумма
            ------------------------------------------------------------
            """;

    /** Template for record in transaction statement */
    private String recordTemplate = "%s | %s%s| %s BYN";

    /** Constructor with parameter for accessing bank and user information */
    public TransactionStatementView(IBankService service, IUserService userService) {
        this.bankService = service;
        this.userService = userService;
    }

    /**
     * Fills transaction statement template with provided values.
     * Adds transaction notes according to operation type and formats transaction amount for print
     *
     * @param account account for which transaction statement will be done
     * @param statementList list of all account transaction records
     * @param intervalOption user selected interval option for transaction statement
     *
     * @return formatted string with account transaction statement
     */
    public String getStatement(Account account, List<StatementDto> statementList, int intervalOption) {
        StringBuilder statement = new StringBuilder();
        String bank = bankService.getBank(account.getBankId()).getName();
        String client = userService.getUser(account.getUserId()).getName();
        LocalDateTime requestDateTime = LocalDateTime.now();
        String requestDate = dateFormatter.format(requestDateTime.toLocalDate());
        String creationDate = dateFormatter.format(account.getCreationDate().toLocalDate());
        String periodStart = getPeriodStart(account, intervalOption, requestDateTime.toLocalDate());
        String amountStringValue = String.format("%.2f", account.getBalance());
        String operationNote;
        double recordAmount;

        statement.append(statementTemplate.formatted(" ".repeat(35 - bank.length() / 2), bank, client,
                account.getBankId(), creationDate, periodStart, requestDate, requestDate,
                timeFormatter.format(requestDateTime.toLocalTime()), amountStringValue));

        for (StatementDto statementDto : statementList) {
            operationNote = getOperationNote(statementDto, client);
            recordAmount = getRecordAmount(statementDto, client);
            statement.append(recordTemplate.formatted(dateFormatter.format(statementDto.time()),
                    operationNote, " ".repeat(41 - operationNote.length()), String.format("%.2f", recordAmount)));
            statement.append("\n");
        }

        return statement.toString().trim();
    }

    /**
     * Returns transaction statement period start according to selected interval option
     *
     * @param account account for which transaction statement will be done
     * @param intervalOption user selected interval option for transaction statement
     * @param requestDate date of transaction statement request
     *
     * @return String with start of period for use in transaction statement.
     * Minimum value — date of account creation
     */
    private String getPeriodStart(Account account, int intervalOption, LocalDate requestDate) {
        LocalDate creationDate = account.getCreationDate().toLocalDate();
        LocalDate periodStart = LocalDate.now();

        switch (intervalOption) {
            case 1 -> periodStart = requestDate.minusMonths(1);
            case 2 -> periodStart = requestDate.minusYears(1);
            case 3 -> periodStart = creationDate;
        }

        if (periodStart.isBefore(creationDate)) {
            periodStart = creationDate;
        }

        return dateFormatter.format(periodStart);
    }

    /**
     * Returns transaction note for use in statement record
     *
     * @param statementDto information about transaction
     * @param accountOwner name of account owner for which transaction statement will be done
     *
     * @return String which describes transaction in statement
     */
    private String getOperationNote(StatementDto statementDto, String accountOwner) {
        if (statementDto.receiver().isEmpty()) {
            return "Снятие средств";
        } else if (statementDto.sender().isEmpty()) {
            return "Пополнение счёта";
        } else if (!statementDto.receiver().equals(accountOwner)) {
            return "Перевод для " + statementDto.receiver().split(" ")[0];
        } else {
            return "Пополнение от " + statementDto.receiver().split(" ")[0];
        }
    }

    /**
     * Formats transaction amount for display in transaction statement according to transaction type
     *
     * @param statementDto information about transaction
     * @param accountOwner name of account owner for which transaction statement will be done
     *
     * @return transaction value prepared for printing in transaction statement.
     * Bigger than zero for incoming transactions, less than zero otherwise
     */
    private double getRecordAmount(StatementDto statementDto, String accountOwner) {
        if (statementDto.receiver().isEmpty() || !statementDto.receiver().equals(accountOwner)) {
            return -1 * statementDto.amount();
        } else {
            return statementDto.amount();
        }
    }
}
