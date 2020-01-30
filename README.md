# Authentication Server

## Условие

Системата се състои от клиентска и сървърна част.

### Сървър

- Сървърът може да обслужва множество клиенти едновременно.
- Сървърът предлага възможност за регистрация в системата. Регистрацията се извършва с *username* (уникален за базата на даден сървър), *password*, *first name*, *last name*, *email*.
- User информацията се пази във файл (ще играе ролята на база от данни за сървъра).
- *first name*, *last name* и *email* полетата на user-a може да се редактират.
- Паролата на user-a може да се reset-ва.
- Клиент може да се аутентикира пред сървъра със своето потребителско име и парола.
- Сесията пази уникален идентификатор и time-to-live(ttl). След изтичане на time-to-live периода, сесията бива унищожена.
Системата създава нова сесия при успешна автентикация с потребителско име и парола и да връща уникално session id на клиента, както и ttl-а на сесията.
- Системата позволява автентикация със session id, когато има успешно създадена сесия за даден user.
- Системата позволява logout по дадено session id, като операцията унищожава съответната сесия.
- При повторен login за даден user с username и password, предишната създадена сесия се терминира и се създава нова.
- Системата предлага опция за изтриване на user, която да изтрива всяка пазена информация в базата за даденият user, както и терминира всички създадени за него сесии.

### Клиент

Клиентската част на приложението има възможността да консумира предлаганите от сървъра операции. Клиентът има следните команди:

```bash
register --username <username> --password <password> --first-name <firstName> --last-name <lastName> --email <email>
login -–username <username> --password <password>
login -–session-id <sessionId>
reset-password –-username <username> --old-password <oldPassword> --new-password <newPassword>
update-user  -–session-id <session-id>  -–new-username <newUsername> --new-first-name <newFirstName> --new-last-name <newLastName> --new-email <email>. Всички параметри освен --session-id в тази команда са опционални.
logout –session-id <sessionId>
delete-user –username <username>
```
