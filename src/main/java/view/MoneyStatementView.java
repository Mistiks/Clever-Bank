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
public class MoneyStatementView {

    /** Instance of service for retrieving bank name from database */
    private final IBankService bankService;

    /** Instance of service for retrieving username from database */
    private final IUserService userService;

    /** Output formatter for dates in money statement */
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** Output formatter for time of money statement formation */
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /** Template for money statement header */
    private String statementTemplate = """
                                    Money statement
            %s%s
            Клиент                        | %s
            Счёт                          | %s
            Валюта                        | BYN
            Дата открытия                 | %s
            Период                        | %s — %s
            Дата и время формирования     | %s, %s
            Остаток                       | %s BYN
                         Приход      |       Уход
                     ---------------------------------
            """;

    /** Template for record in money statement */
    private String recordTemplate = "%s%s BYN   |   %s BYN";

    /** Constructor with parameter for accessing bank and user information */
    public MoneyStatementView(IBankService service, IUserService userService) {
        this.bankService = service;
        this.userService = userService;
    }

    /**
     * Fills money statement template with provided values.
     *
     * @param account account for which money statement will be done
     * @param statementList list of all account transaction records
     * @param intervalStart user selected interval start for money statement
     *
     * @return formatted string with account money statement
     */
    public String getStatement(Account account, List<StatementDto> statementList, LocalDate intervalStart) {
        StringBuilder statement = new StringBuilder();
        String bank = bankService.getBank(account.getBankId()).getName();
        String client = userService.getUser(account.getUserId()).getName();
        LocalDateTime requestDateTime = LocalDateTime.now();
        String requestDate = dateFormatter.format(requestDateTime.toLocalDate());
        String creationDate = dateFormatter.format(account.getCreationDate().toLocalDate());
        String periodStart = dateFormatter.format(intervalStart);
        String amountStringValue = String.format("%.2f", account.getBalance());
        double income = 0;
        double outcome = 0;

        statement.append(statementTemplate.formatted(" ".repeat(35 - bank.length() / 2), bank, client,
                account.getBankId(), creationDate, periodStart, requestDate, requestDate,
                timeFormatter.format(requestDateTime.toLocalTime()), amountStringValue));

        for (StatementDto statementDto : statementList) {
            if (getOperationType(statementDto, client)) {
                income += statementDto.amount();
            } else {
                outcome -= statementDto.amount();
            }
        }

        statement.append(recordTemplate.formatted(" ".repeat(18 - String.format("%.2f", income).length()),
                String.format("%.2f", income), String.format("%.2f", outcome))).append("\n");

        return statement.toString().stripTrailing();
    }

    /**
     * Returns operation type for use in statement record
     *
     * @param statementDto information about transaction
     * @param accountOwner name of account owner for which transaction statement will be done
     *
     * @return true in case of incoming funds, else otherwise
     */
    private boolean getOperationType(StatementDto statementDto, String accountOwner) {
        return !statementDto.receiver().isEmpty() && statementDto.receiver().equals(accountOwner);
    }
}