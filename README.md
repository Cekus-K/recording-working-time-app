Java, Spring, PostgreSQL, Gradle, JUnit5, Mockito2

1 . USER STORIES USERS:

a) As an administrator, I can create a user.

- Login - unique / required
- Name - required
- Surname - required
- Administrator / Manager / Employee type - required
- Password - required
- Email - required / correct format
- Cost per hour of work - required, greater than 0

b) As an administrator, I can edit a user - all fields can be changed

c) As an administrator, I can delete a user

d) As an administrator, I can view the user table

e) As an administrator, I can filter the user table (login, first and last name, cost from-to, user type).

2 . USER STORIES PROJECTS:

a) As a manager, I can create a project

- Name - required, unique
- Description - not required
- Duration - date range from / to
- Project budget - required greater than 0

b) As a manager, I can assign an employee to a project (an employee can be assigned to many projects)

c) As a manager, I can write off an employee from the project.
  
d) As a manager, I can edit a project

e) As a manager, I can delete a project

f) As a manager, I can display a table with projects (along with a column with a list of people assigned to the project and the percentage of budget used)

g) As a manager, I can filter out projects:

- Name
- Duration from-to
- Selecting several users from the list.
- Budget exceeded Yes / No

3 . ENTERING THE RECORDS:

a) As a user, I can enter timesheets:

- Project (selected from those available to me) - required
- Start date and time
- End date and time

b) As a user, I can see my timesheets (list of entries)

c) As a user, I can delete specific timesheets

d) As a user, I can edit specific timesheets

4 . REPORTS:

a) As a manager, I can view user data in the time range (from the beginning, from the year, from the month and from the week). The returned data is to contain information about the total cost of the employee from a given period, projects in which he participated and a summary of how much time he spent in a specific project and what was the cost of his work

b) As a manager, I can view data on the project in terms of time (from the beginning, from the year, from the month and from the week) - what people participated in it, how many hours they spent in it, what was the cost of a specific employee and a summary of project costs and hours spent on it . Additionally, there should be information on whether the assumed budget for the project has already been exceeded.
