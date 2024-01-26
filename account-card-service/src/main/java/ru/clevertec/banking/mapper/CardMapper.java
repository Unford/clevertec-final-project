package ru.clevertec.banking.mapper;

import org.mapstruct.*;
import ru.clevertec.banking.dto.card.*;
import ru.clevertec.banking.entity.Card;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {

    @Mapping(target = "cardNumber", source = "request.card_number")
    @Mapping(target = "customerId", source = "request.customer_id")
    @Mapping(target = "customerType", source = "request.customer_type")
    @Mapping(target = "cardStatus", source = "request.card_status")
    Card fromRequest(CardRequest request);

    @Mapping(target = "card_number", source = "card.cardNumber")
    @Mapping(target = "customer_id", source = "card.customerId")
    @Mapping(target = "customer_type", source = "card.customerType")
    @Mapping(target = "card_status", source = "card.cardStatus")
    @Mapping(target = "card_number_readable", expression = "java(card.getCardNumber().replaceAll(\"(.{4})(?=.{4})\",\"$1 \"))")
    CardResponse toResponse(Card card);

    List<CardResponse> toListResponse(List<Card> cards);

    @Mapping(target = "card.customerType", source = "request.customer_type",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.cardStatus", source = "request.card_status",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.iban", source = "request.iban",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Card updateFromRequest(CardRequestForUpdate request, @MappingTarget Card card);

    @Mapping(target = "card_number", source = "card.cardNumber")
    @Mapping(target = "customer_id", source = "card.customerId")
    @Mapping(target = "customer_type", source = "card.customerType")
    @Mapping(target = "card_status", source = "card.cardStatus")
    @Mapping(target = "card_number_readable", expression = "java(card.getCardNumber().replaceAll(\"(.{4})(?=.{4})\",\"$1 \"))")
    @Mapping(target = "card_balance", source = "balance")
    CardCurrencyResponse toCardWithBalance(Card card, Balance balance);

    @Mapping(target = "card.cardNumber", source = "message.card_number",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.iban", source = "message.iban",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.customerId", source = "message.customer_id",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.customerType", source = "message.customer_type",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.cardholder", source = "message.cardholder",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "card.cardStatus", source = "message.card_status",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Card updateFromMessage(CardRequest message, @MappingTarget Card card);
}
