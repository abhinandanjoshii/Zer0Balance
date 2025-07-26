package com.consoleadmin.zer0balance.service;

import com.consoleadmin.zer0balance.dto.ExpenseDTO;
import com.consoleadmin.zer0balance.entity.ProfileEntity;
import com.consoleadmin.zer0balance.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${zer0.balance.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *", zone = "IST")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Cron Job started: sendDailyIncomeExpenseReminder()");
        List<ProfileEntity> profiles = profileRepository.findAll();

        for (ProfileEntity profile : profiles) {
            String body = "<div style='max-width:600px;margin:auto;border-radius:10px;overflow:hidden;"
                    + "box-shadow:0 5px 15px rgba(0,0,0,0.1);font-family:Segoe UI,sans-serif;'>"

                    // Header
                    + "<div style='background: linear-gradient(135deg, #43cea2, #185a9d); padding: 20px;'>"
                    + "<h2 style='color:white; text-align:center; margin:0;'>⏰ Daily Reminder</h2></div>"

                    // message
                    + "<div style='padding: 20px; color:#333;'>"
                    + "<p>Hey <strong>" + profile.getFullName() + "</strong>,</p>"
                    + "<p>Just a friendly reminder to log your income and expenses for <strong>today</strong> in <strong>Zer0Balance</strong>.</p>"

                    + "<div style='text-align:center;margin:30px 0;'>"
                    + "<a href='" + frontendUrl + "' style='display:inline-block;padding:12px 24px;"
                    + "background: linear-gradient(135deg, #667eea, #764ba2);"
                    + "color:#fff;text-decoration:none;border-radius:30px;font-weight:bold;font-size:16px;'>"
                    + "📲 Add Now on Zer0Balance</a></div>"

                    + "<p style='margin-top:20px;'>Stay consistent — little steps daily make big financial wins! 🚀</p>"
                    + "<p>Warm regards,<br/><strong style='color:#185a9d;'>Zer0Balance Team</strong></p>"
                    + "</div></div>";

            emailService.sendEmail(
                    profile.getEmail(),
                    "Zer0Balance - Daily Reminder: Add Your Income & Expenses",
                    body
            );
        }

        log.info("Cron Job ended: sendDailyIncomeExpenseReminder()");
    }


    @Scheduled(cron = "0 0 23 * * *", zone = "IST")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        for (ProfileEntity profile : profiles) {
            List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), today);

            if (!todaysExpenses.isEmpty()) {
                StringBuilder table = new StringBuilder();

                table.append("<style>")
                        .append("table { border-collapse: collapse; width: 100%; font-family: 'Segoe UI', sans-serif; }")
                        .append("th, td { padding: 12px; text-align: left; }")
                        .append("th { background: linear-gradient(135deg, #667eea, #764ba2); color: white; border: none; }")
                        .append("tr:nth-child(even) { background-color: #f9f9f9; }")
                        .append("tr:nth-child(odd) { background-color: #ffffff; }")
                        .append("tr:hover { background-color: #f1f1f1; }")
                        .append("td { border-bottom: 1px solid #ddd; }")
                        .append("</style>");

                table.append("<table>");
                table.append("<tr>")
                        .append("<th>No.</th>")
                        .append("<th>Name</th>")
                        .append("<th>Amount (₹)</th>")
                        .append("<th>Category</th>")
                        .append("</tr>");

                int i = 1;
                for (ExpenseDTO expense : todaysExpenses) {
                    table.append("<tr>")
                            .append("<td>").append(i++).append("</td>")
                            .append("<td>").append(expense.getName()).append("</td>")
                            .append("<td>").append(String.format("%.2f", expense.getAmount())).append("</td>")
                            .append("<td>").append(expense.getCategoryId() != null ? expense.getCategoryName() : "N/A").append("</td>")
                            .append("</tr>");
                }
                table.append("</table>");

                String body = "<div style='max-width:600px;margin:auto;border-radius:10px;overflow:hidden;"
                        + "box-shadow:0 5px 15px rgba(0,0,0,0.1);font-family:Segoe UI,sans-serif;'>"
                        + "<div style='background: linear-gradient(135deg, #667eea, #764ba2); padding: 20px;'>"
                        + "<h2 style='color:white; text-align:center; margin:0;'>💸 Expense Summary</h2></div>"
                        + "<div style='padding: 20px; color:#333;'>"
                        + "<p>Hey <strong>" + profile.getFullName() + "</strong>,</p>"
                        + "<p>Here's your expense summary for today : <strong>" + today + "</strong>:</p>"
                        + table
                        + "<p style='margin-top:20px;'>✅ Keep tracking your expenses daily — smart money moves start with awareness!</p>"
                        + "<p>Cheers,<br/><strong style='color:#764ba2;'>Zer0Balance Team</strong></p>"
                        + "</div></div>";

                emailService.sendEmail(
                        profile.getEmail(),
                        "Zer0Balance: Your Daily Expense Summary",
                        body
                );
            }
        }

        log.info("Job completed: sendDailyExpenseSummary()");
    }
}
