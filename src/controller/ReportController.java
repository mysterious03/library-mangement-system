package controller;

import model.User;
import service.ReportService;

import java.util.List;
import java.util.Map;

/**
 * Controller for rendering the 10 Advanced SQL Analytical Reports.
 * Member 4: Reports & Analytics Module.
 */
public class ReportController {

    private final ReportService reportService = new ReportService();

    public String renderReports(User user, int reportId, String returnRoute) {
        if (reportId < 1 || reportId > 10) reportId = 1;
        List<Map<String, String>> data = reportService.getReport(reportId);
        String reportTitle = reportService.getReportTitle(reportId);

        StringBuilder content = new StringBuilder();

        // 1. Report Selector Toolbar
        content.append("<div class=\"filter-bar\" style=\"margin-bottom: 1.5rem;\">\n")
            .append("  <form method=\"GET\" action=\"").append(returnRoute).append("\" class=\"filter-form\">\n")
            .append("    <div class=\"filter-field\" style=\"flex: 3 1 350px;\">\n")
            .append("      <label class=\"form-label\">Select SQL Analytical Report (1 to 10)</label>\n")
            .append("      <select name=\"id\" class=\"form-select\" onchange=\"this.form.submit()\">\n");

        for (int i = 1; i <= 10; i++) {
            boolean sel = (i == reportId);
            content.append("        <option value=\"").append(i).append("\" ").append(sel ? "selected" : "").append(">").append(reportService.getReportTitle(i)).append("</option>\n");
        }

        content.append("      </select>\n    </div>\n")
            .append("    <div>\n")
            .append("      <button type=\"submit\" class=\"btn btn-primary\">Generate Report</button>\n")
            .append("    </div>\n")
            .append("  </form>\n</div>\n");

        // 2. Report Table
        content.append("<div class=\"card-table\">\n")
            .append("  <div class=\"table-header\">\n")
            .append("    <h2>").append(reportTitle).append(" (").append(data.size()).append(" rows)</h2>\n")
            .append("    <span style=\"font-size: 0.8rem; color: var(--text-muted);\">Executed via Live SQL Aggregation &amp; Subqueries</span>\n")
            .append("  </div>\n")
            .append("  <div class=\"data-table-container\">\n");

        if (data.isEmpty()) {
            content.append("    <div style=\"padding: 2.5rem; text-align: center; color: var(--text-muted);\">No records match this report criteria.</div>\n");
        } else {
            content.append("    <table class=\"data-table\">\n      <thead>\n        <tr>\n");
            Map<String, String> firstRow = data.get(0);
            for (String col : firstRow.keySet()) {
                content.append("          <th>").append(HtmlRenderer.escape(col)).append("</th>\n");
            }
            content.append("        </tr>\n      </thead>\n      <tbody>\n");

            for (Map<String, String> row : data) {
                content.append("        <tr>\n");
                for (String val : row.values()) {
                    content.append("          <td>").append(HtmlRenderer.escape(val)).append("</td>\n");
                }
                content.append("        </tr>\n");
            }

            content.append("      </tbody>\n    </table>\n");
        }

        content.append("  </div>\n</div>\n");

        return HtmlRenderer.renderAppPage(reportTitle, returnRoute, user, null, null, content.toString());
    }
}
