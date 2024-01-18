package ru.clevertec.banking.deposit.util;

import net.datafaker.Faker;
import ru.clevertec.banking.deposit.model.CustomerType;
import ru.clevertec.banking.deposit.model.DepositType;
import ru.clevertec.banking.deposit.model.TermScale;
import ru.clevertec.banking.deposit.model.domain.AccountInfo;
import ru.clevertec.banking.deposit.model.domain.Deposit;
import ru.clevertec.banking.deposit.model.domain.DepositInfo;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessage;
import ru.clevertec.banking.deposit.model.dto.message.DepositMessagePayload;
import ru.clevertec.banking.deposit.model.dto.request.CreateAccountInfoRequest;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositInfoRequest;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.AccountInfoResponse;
import ru.clevertec.banking.deposit.model.dto.response.DepositInfoResponse;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


public class RandomDepositFactory {
    private final Faker faker;

    public RandomDepositFactory(Faker faker) {
        this.faker = faker;
    }

    public DepositMessage.MessageHeader getDepositMessageHeader() {
        return new DepositMessage.MessageHeader().setMessageType("deposit_info");
    }


    public Deposit createDeposit() {
        return new Deposit()
                .setAccInfo(this.createAccInfo())
                .setDepInfo(this.createDepositInfo())
                .setId((long) faker.number().positive())
                .setCustomerId(UUID.randomUUID())
                .setDeleted(faker.bool().bool())
                .setCustomerType(faker.options().option(CustomerType.class));
    }

    public AccountInfo createAccInfo() {
        return new AccountInfo()
                .setAccIban(faker.finance().iban("BY"))
                .setAccOpenDate(faker.date().birthdayLocalDate())
                .setCurrAmount(BigDecimal.valueOf(faker.number()
                        .randomDouble(2, 0, 100000)))
                .setCurrAmountCurrency(faker.currency().code());
    }

    public DepositInfo createDepositInfo() {
        return new DepositInfo()
                .setRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
                .setTermVal(faker.number().numberBetween(1, 100))
                .setDepType(faker.options().option(DepositType.class))
                .setAutoRenew(faker.bool().bool())
                .setExpDate(faker.date().future(200, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                .setTermScale(faker.options().option(TermScale.class));
    }

    public DepositMessage createDepositMessage() {
        return new DepositMessage()
                .setHeader(getDepositMessageHeader())
                .setPayload(this.createDepositMessagePayload());
    }

    public DepositMessagePayload createDepositMessagePayload() {
        return new DepositMessagePayload()
                .setAccInfo(new DepositMessagePayload.MessageAccountInfo()
                        .setAccIban(faker.finance().iban("BY"))
                        .setAccOpenDate(faker.date().birthdayLocalDate())
                        .setCurrAmount(BigDecimal.valueOf(faker.number()
                                .randomDouble(2, 0, 100000)))
                        .setCurrAmountCurrency(faker.currency().code()))
                .setDepInfo(new DepositMessagePayload.MessageDepositInfo()
                        .setRate(BigDecimal.valueOf(faker.number()
                                .randomDouble(2, 1, 100)))
                        .setTermVal(faker.number().numberBetween(1, 100))
                        .setDepType(faker.options().option(DepositType.class))
                        .setAutoRenew(faker.bool().bool())
                        .setExpDate(faker.date().future(200, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                        .setTermScale(faker.options().option(TermScale.class)))
                .setCustomerId(UUID.randomUUID())
                .setCustomerType(faker.options().option(CustomerType.class));
    }

    public CreateDepositRequest createDepositRequest() {
        return new CreateDepositRequest()
                .setAccInfo(new CreateAccountInfoRequest()
                        .setAccIban(faker.finance().iban("BY"))
                        .setAccOpenDate(faker.date().birthdayLocalDate())
                        .setCurrAmount(BigDecimal.valueOf(faker.number()
                                .randomDouble(2, 0, 100000)))
                        .setCurrAmountCurrency(faker.currency().code()))
                .setDepInfo(new CreateDepositInfoRequest()
                        .setRate(BigDecimal.valueOf(faker.number()
                                .randomDouble(2, 1, 100)))
                        .setTermVal(faker.number().numberBetween(1, 100))
                        .setDepType(faker.options().option(DepositType.class))
                        .setAutoRenew(faker.bool().bool())
                        .setExpDate(faker.date().future(200, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                        .setTermScale(faker.options().option(TermScale.class)))
                .setCustomerId(UUID.randomUUID())
                .setCustomerType(faker.options().option(CustomerType.class));
    }

    public UpdateDepositRequest createUpdateDepositRequest() {
        return new UpdateDepositRequest()
                .setDepInfo(new UpdateDepositRequest.UpdateDepositInfoRequest()
                        .setDepType(faker.options().option(DepositType.class))
                        .setAutoRenew(faker.bool().bool()));
    }


    public DepositResponse createDepositResponse() {
        return new DepositResponse()
                .setAccInfo(new AccountInfoResponse()
                        .setAccIban(faker.finance().iban("BY"))
                        .setAccOpenDate(faker.date().birthdayLocalDate())
                        .setCurrAmount(BigDecimal.valueOf(faker.number()
                                .randomDouble(2, 0, 100000)))
                        .setCurrAmountCurrency(faker.currency().code())
                )
                .setDepInfo(new DepositInfoResponse()
                        .setRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
                        .setTermVal(faker.number().numberBetween(1, 100))
                        .setDepType(faker.options().option(DepositType.class))
                        .setAutoRenew(faker.bool().bool())
                        .setExpDate(faker.date().future(200, TimeUnit.DAYS).toLocalDateTime().toLocalDate())
                        .setTermScale(faker.options().option(TermScale.class))
                )
                .setId((long) faker.number().positive())
                .setCustomerId(UUID.randomUUID())
                .setCustomerType(faker.options().option(CustomerType.class));
    }
}
