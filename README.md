# Tea-Cup Loan System  
A mini project (hence the name) created as a way for me  to accustom myself to the practices of TDD, OOP, domain-driven design, & benchmarking and to have a hands-on experience with micro-services built & set-up by tech stacks of the following:  
 - [X] SpringBoot
 - [X] Postgres + Sqitch change management
 - [ ] JSON RPC
 - [ ] Cache
 - [ ] Docker 
 - [ ] CI/CD - later on deployed on AWS

![Unit Tests](https://github.com/sheilarezkia/teacup-loan-system/actions/workflows/unit-tests.yml/badge.svg)
![Build](https://github.com/sheilarezkia/teacup-loan-system/actions/workflows/build.yml/badge.svg)

### An Overview
The app you'll be seeing is a dummy design of how I understand/imagine a loan system under the grand scheme, where simply put you have loan accounts making purchases, resulting in multiple payments that each of those loan account needs to complete.
With this simple requirement, I've decided to create 3 services, **accounts**, **purchases**, and **payments**. Each of these are expected to run on individual machines, communicating with each other using RPC calls.

### A Closer Look & Design
#### Account
[![Coverage - Accounts](https://sonarcloud.io/api/project_badges/measure?project=srezkia_accounts&metric=coverage)](https://sonarcloud.io/dashboard?id=srezkia_accounts)
 
Trying to mimic loan accounts in general, each `account` record has the name of the account holder, the status of the account. Each account holds its maximum limit, which denotes the maximum sum of purchases the customer may make at a given time.
 
#### Purchase
 [![Coverage - Purchases](https://sonarcloud.io/api/project_badges/measure?project=srezkia_purchases&metric=coverage)](https://sonarcloud.io/dashboard?id=srezkia_purchases)
 
Whenever a customer with a loan account submits an application form -- complete with a set of repayment schedules that the customer sets up, the system will first check if that purchase exceeds the current maximum limit that the account holds. A purchase requesting a loan higher than the maximum limit will automatically result in a rejection.
Otherwise, a new `purchase` will be created for that application. A creation of a `purchase` will automatically create a number of `payment` records that the customer needs to complete before each of the due date. 
A newly-created `purchase` record then will have a status of `loan_disbursed`, which will be changed later after the last payment is completed.

Upon collection of the last repayment, if the `purchase` status may is set to `closed`, we need to update the maximum limit of that `account`, adding the amount loan previously disbursed to the customer holding that account.

#### Payment
[![Coverage - Payments](https://sonarcloud.io/api/project_badges/measure?project=srezkia_payments&metric=coverage)](https://sonarcloud.io/dashboard?id=srezkia_payments)

Each payment record has a due date as the time at which the customer has to pay them by the latest. When the last payment of the purchase has successfully been paid, the system will check for any late payments. If the customer has always managed to pay before the due date, the `purchase` record status will be `closed`, whereas if there's any late payments made after their due dates, the `purchase` status will be marked as `penalty_fees_collection`.

### Running the Services Locally

#### Running with Docker

#### Deployment