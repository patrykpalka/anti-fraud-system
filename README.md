# Anti-Fraud System

This is my implementation of Anti-Fraud System project from Hyperskill.

## Learning Outcomes
Create a RESTful web service using SpringBoot, learn the basics of user authentication and authorization. Get to know the fundamentals of fraud detection and rule-based systems.

This project demonstrates (in a simplified form) the principles of anti-fraud systems in the financial sector. 
For this project, we will work on a system with an expanded role model, a set of REST endpoints responsible for interacting with users, and an internal transaction validation logic based on a set of heuristic rules.

## Transaction Feedback and Validity Table

<table border="1" cellpadding="1" cellspacing="1" style="width: 700px;">
<tbody>
<tr>
<th>
<table>
<tbody>
<tr>
<th>Transaction Feedback →</th>
</tr>
<tr>
<th style="text-align: left;">Transaction Validity ↓</th>
</tr>
</tbody>
</table>
</th>
<th>ALLOWED</th>
<th>MANUAL_PROCESSING</th>
<th>PROHIBITED</th>
</tr>
<tr>
<th>ALLOWED</th>
<td>Exception</td>
<td>↓ max ALLOWED</td>
<td>
<p>↓ max ALLOWED</p>
<p>↓ max MANUAL</p>
</td>
</tr>
<tr>
<th>MANUAL_PROCESSING</th>
<td>↑ max ALLOWED</td>
<td>Exception</td>
<td>↓ max MANUAL</td>
</tr>
<tr>
<th>PROHIBITED</th>
<td>
<p>↑ max ALLOWED</p>
<p>↑ max MANUAL</p>
</td>
<td>↑ max MANUAL</td>
<td>Exception</td>
</tr>
</tbody>
</table>

## Limit Adjustment Formulas

**Formula for increasing the limit:**
```plaintext
new_limit = 0.8 * current_limit + 0.2 * value_from_transaction
```

**Formula for decreasing the limit:**
```plaintext
new_limit = 0.8 * current_limit - 0.2 * value_from_transaction
```
## Role Model and API Access Permissions
<table border="1" cellpadding="1" cellspacing="1">
<tbody>
<tr>
<td> </td>
<td>Anonymous</td>
<td>MERCHANT</td>
<td>ADMINISTRATOR</td>
<td>SUPPORT</td>
</tr>
<tr>
<td>POST /api/auth/user</td>
<td>+</td>
<td>+</td>
<td>+</td>
<td>+</td>
</tr>
<tr>
<td>DELETE /api/auth/user</td>
<td>-</td>
<td>-</td>
<td>+</td>
<td>-</td>
</tr>
<tr>
<td>GET /api/auth/list</td>
<td>-</td>
<td>-</td>
<td>+</td>
<td>+</td>
</tr>
<tr>
<td>POST /api/antifraud/transaction</td>
<td>-</td>
<td>+</td>
<td>-</td>
<td>-</td>
</tr>
<tr>
<td>/api/antifraud/suspicious-ip</td>
<td>-</td>
<td>-</td>
<td>-</td>
<td>+</td>
</tr>
<tr>
<td>/api/antifraud/stolencard</td>
<td>-</td>
<td>-</td>
<td>-</td>
<td>+</td>
</tr>
<tr>
<td>GET /api/antifraud/history</td>
<td>-</td>
<td>-</td>
<td>-</td>
<td>+</td>
</tr>
<tr>
<td>PUT /api/antifraud/transaction</td>
<td>-</td>
<td>-</td>
<td>-</td>
<td>+</td>
</tr>
</tbody>
</table>

Here's the link to the project: https://hyperskill.org/projects/232

Check out my profile: https://hyperskill.org/profile/595408716
