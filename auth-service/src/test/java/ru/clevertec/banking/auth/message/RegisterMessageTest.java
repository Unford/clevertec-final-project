package ru.clevertec.banking.auth.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.clevertec.banking.auth.dto.message.RegisterMessage;
import ru.clevertec.banking.auth.dto.message.RegisterMessagePayload;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockitoExtension.class)
class RegisterMessageTest {

    @Test
    @DisplayName("Should create RegisterMessage")
    void shouldCreateRegisterMessage() {
        RegisterMessagePayload payload = new RegisterMessagePayload(UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"), "email@email.ru");
        RegisterMessage.MessageHeader messageHeader = new RegisterMessage.MessageHeader();
        messageHeader.setMessageType("register");
        RegisterMessage registerMessage = new RegisterMessage(messageHeader, payload);

        assertEquals("register", registerMessage.getHeader().getMessageType());
        assertEquals("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729", registerMessage.getPayload().getId().toString());
        assertEquals("email@email.ru", registerMessage.getPayload().getEmail());
    }

    @Test
    @DisplayName("Should create RegisterMessage with setters")
    void shouldCreateRegisterMessageWithSetters() {
        RegisterMessagePayload payload = new RegisterMessagePayload();
        payload.setId(UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"));
        payload.setEmail("email@email.ru");

        RegisterMessage.MessageHeader messageHeader = new RegisterMessage.MessageHeader();
        messageHeader.setMessageType("register");

        RegisterMessage registerMessage = new RegisterMessage();
        registerMessage.setPayload(payload);
        registerMessage.setHeader(messageHeader);

        assertEquals("register", registerMessage.getHeader().getMessageType());
        assertEquals("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729", registerMessage.getPayload().getId().toString());
        assertEquals("email@email.ru", registerMessage.getPayload().getEmail());
    }

    @Test
    @DisplayName("testEqualMethodForMessageHeader")
    void testEqualMethodForMessageHeader() {
        RegisterMessage.MessageHeader messageHeader = new RegisterMessage.MessageHeader();
        messageHeader.setMessageType("register");

        assertNotEquals(new RegisterMessage.MessageHeader(), messageHeader);
        assertEquals(messageHeader, messageHeader);
        assertNotEquals(null, messageHeader);
        assertNotEquals(new RegisterMessage(), messageHeader);
    }
}
