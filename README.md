#### Story 1: Create Account for the customer

- Input:
  - Address( Street, city , pin),
  - First name, last name
  - AADHAR/SSN or some sort of unique identifier for customer
- Flow:
  - Check the credit history of the customer based on unique id.
  - Create customer.
  - Create account for the customer
- Constraints
  - An account can not exists without a customer
  - A customer can not exist without an account for more than a day
  - Customer can not be created for credit score below 100
  - For a given unique identifier, only a single customer can be created


#### Story 2: Create a joint account for customer

- Input:
  - There will be two customers.
  - The input for each customer will be same as story 1
- Flow:
  - Same as Story 1
- Constraints:
  - A joint account needs to have at least two customers

#### Story 3: Update address

- Input:
  - Customer ID and new Address
- Flow:
  - Update the address for the given customer id

#### Story 4: Batch Record creation

- Input:
  - A file consisting of customer and account records.
  - Customer record format: ID | First name |  Last name | Street, city, state, country| SSN/Aadhar etc
  - Account Record format: Account ID | Customer ID| Balance
- Flow:
  - Read the records from the file and create customer and their related accounts on platform
- Constraint:
  - The batch size is 100 GB and the batch has to finish in 1 min.

#### Story 5: Create a report of customer details

- Format of the report
  - Customer ID | First name , Last Name| \[Account Id1, Account Id2\]
