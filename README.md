# telegram-bot

Some code sample:

```java
        if (update.hasCallbackQuery()) {
            var callbackQuery = update.getCallbackQuery();
            var data = callbackQuery.getData();
            var senderId = callbackQuery.getFrom().getId();
            if ("reply_button1".equals(data)) {
                var keyboardM2 = ForceReplyKeyboard.builder()
                        .inputFieldPlaceholder("Reply")
                        .forceReply(true)
                        .build();
                var result = sendMenu(senderId, "Reply", keyboardM2);
                var msgId = result.getMessageId();
                log.info("message id is:{}", msgId);
                db.put(msgId, "EXPENSE_REASON");
            } else if ("reply_button2".equals(data)) {
                var keyboardM3 = ForceReplyKeyboard.builder()
                        .inputFieldPlaceholder("Reply")
                        .forceReply(true)
                        .build();
                var result = sendMenu(senderId, "Reply", keyboardM3);
                var msgId = result.getMessageId();
                db.put(msgId, "EXPENSE_AMOUNT");
            }
            return;
        }

        var senderId = message.getFrom().getId();

        
        if (isDailySummary(message.getText())) {
                var reason = InlineKeyboardButton.builder()
                        .text("Reply")
                        .callbackData("reply_button1")
                        .build();
                var reasonReply = InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(reason)).build();

                sendMenu(senderId, "What is the expense reason?", reasonReply);
            }
        else {
            var isReply = message.isReply();
            if (isReply) {
                log.info("is reply");
                var replyToMessageId = message.getReplyToMessage().getMessageId();
                log.info("replyToMessageId: {}", replyToMessageId);
                if (Objects.equals(db.get(replyToMessageId), "EXPENSE_REASON")) {
                    var amount = InlineKeyboardButton.builder()
                            .text("Reply")
                            .callbackData("reply_button2")
                            .build();
                    var amountReply = InlineKeyboardMarkup.builder()
                            .keyboardRow(List.of(amount)).build();

                    sendMenu(senderId, "What is the expense amount?", amountReply);
                } else if (Objects.equals(db.get(replyToMessageId), "EXPENSE_AMOUNT")) {
                    sendText(senderId, "Done Thank you");
                }
            } else {
                sendText(senderId, "Please Input command");
            }
        }

       
```
