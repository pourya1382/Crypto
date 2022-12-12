# Crypto

![Bitcoin](https://user-images.githubusercontent.com/115644088/207050891-d47d1eee-ccf0-49b6-8f01-dd44947440d4.png)



# **About The Project**

This is a test project for get information of Cryptocurrencie Market from Finearz API and do some work. In this project use spring boot version 3 and java version 17.
### What DO Program! 
- GET information market in json format with http request.
+ READ json.
* SAVE that in a table.
- SEND email if matket exceed a certain limit, send an email to specific address.
+ CREATE API for get, sort and search in market.

### Built With 

![SoringBoot](https://user-images.githubusercontent.com/115644088/207056149-750b9c75-2cb5-4f7d-b70f-b3ee7fa42aeb.svg)

# Getting Started

### Installation
1.Git clone
> git clone https://github.com/pourya1382/Crypto.git

2. change application.properties code with your database and your email that send text
```
spring.datasource.url=jdbc:postgresql://localhost:5432/name_database
spring.datasource.username=username
spring.datasource.password=********
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.format_sql=true

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username= email address
spring.mail.password= password(gmail>manage your account>security>app password)
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
# Usage
get market changes and see Cryptos table that useful for better choice to buy Crypto.
#ReadMap
- [x] Get market
- [x] Save data and changing every 20 seconds.
- [x] Send email for %2 changing.
- [ ] Get mid, max and mid price.
- [ ] Get change last 1h,4h,12h.
- [ ] Calculate price change in every 1h.

...

# Contact
pourya _ pourya1382@gmail.com
Project Link: https://github.com/pourya1382/Crypto
