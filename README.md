📧 Milou — A Minimal Yet Powerful Email Service
Milou is a lightweight email platform inspired by Tintin’s adorable dog.
Users can sign up, log in, send, read, reply, and forward emails — all in a simple terminal-based interface.
It’s designed to be clean, intuitive, and fun.

🚀 Features
Sign Up & Login

Users can register with just their username or full email.

Automatic email domain completion (@milou.com).

Validation for duplicate emails and password length.

Inbox Management

View all emails, unread emails, or sent emails.

Emails are sorted from newest to oldest.

Each email has a unique 6-character code for easy reference.

Send Emails

Single or multiple recipients.

Auto-generated email code upon sending.

Read Emails by Code

Only accessible if you’re the sender or a recipient.

Once opened, the email is removed from the unread list.

Reply to Emails

Automatically includes all original recipients.

Subject is automatically prefixed with [Re].

Forward Emails

Send an existing email to new recipients.

Subject is automatically prefixed with [Fw].

🖥 Usage Flow
1. Start the App
css
Copy
Edit
[L]ogin, [S]ign up:
2. Sign Up
yaml
Copy
Edit
Name:
Email:         # Optional: just the username, domain is added automatically
Password:      # Must be at least 8 characters
Errors:

Email already exists.

Password too short.

On success:

pgsql
Copy
Edit
Your new account is created.
Go ahead and login!
3. Login
yaml
Copy
Edit
Email:         # Username or full address
Password:
On success:

php-template
Copy
Edit
Welcome back, <Name>!
Unread emails are displayed:

java
Copy
Edit
Unread Emails:

3 unread emails:
+ mamad@milou.com - Our new meeting (1bc170)
+ raees@milou.com - Book suggestions (fnjd1o)
+ nika@milou.com - New feature (12dsb1)
📩 Commands
Once logged in:

css
Copy
Edit
[S]end, [V]iew, [R]eply, [F]orward:
Send an Email
makefile
Copy
Edit
Recipient(s):  # Comma-separated
Subject:
Body:
Result:

css
Copy
Edit
Successfully sent your email.
Code: 1ab97h
View Emails
css
Copy
Edit
[A]ll emails, [U]nread emails, [S]ent emails, Read by [C]ode:
Read by Code
makefile
Copy
Edit
Code:
Recipient(s): ...
Subject: ...
Date: YYYY-MM-DD

<Body>
If unauthorized:

arduino
Copy
Edit
You cannot read this email.
Reply
makefile
Copy
Edit
Code:
Body:
Result:

css
Copy
Edit
Successfully sent your reply to email <code>.
Code: <new_code>
Forward
css
Copy
Edit
Code:
Recipient(s):
Result:

css
Copy
Edit
Successfully forwarded your email.
Code: <new_code>
🛠 Technical Notes
All emails have:

Code (6-char unique string)

Sender

Recipient(s)

Subject

Body

Date sent

Emails are stored chronologically; lists are shown newest first.

Reply and Forward automatically adjust the subject format.

🐶 Why "Milou"?
In tribute to Milou, Tintin’s loyal white fox terrier.
Like Milou, this app is small, fast, and always delivers messages.
