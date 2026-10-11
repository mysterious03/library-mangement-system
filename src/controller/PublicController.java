package controller;

import model.User;
import service.AuthService;

import java.util.Map;

/**
 * Controller for Public facing pages (Home, About, Login, Student Registration).
 * Pure HTML5 + CSS3.
 * Member 1: Authentication & Public Portal.
 */
public class PublicController {

    private final AuthService authService = new AuthService();

    public String renderHome() {
        StringBuilder html = new StringBuilder();

        // =========================================================================
        // EDITORIAL HERO SECTION: Asymmetrical Layout with Tactile Spotlight Card
        // =========================================================================
        html.append("<!-- ========================================================================== -->\n")
            .append("<!-- ASYMMETRICAL EDITORIAL HERO: Oxford / Ivy League Bespoke Aesthetics       -->\n")
            .append("<!-- ========================================================================== -->\n")
            .append("<section class=\"hero-campus-banner\">\n")
            .append("  <div class=\"hero-editorial-grid\">\n")
            .append("    <!-- Left Column: Editorial Manifesto & Live Status -->\n")
            .append("    <div>\n")
            .append("      <div class=\"status-pill-live\">\n")
            .append("        <span class=\"live-dot\"></span>\n")
            .append("        <span>Campus Central Library &bull; Academic Year 2026-2027</span>\n")
            .append("      </div>\n")
            .append("      <h1 class=\"hero-editorial-title\">\n")
            .append("        The Academic Sanctuary<br>\n")
            .append("        <span class=\"title-italic-accent\">of Curated Knowledge</span>\n")
            .append("      </h1>\n")
            .append("      <p class=\"hero-editorial-subtitle\">\n")
            .append("        An institutional digital circulation, cataloging, and analytical reporting platform " +
                    "engineered with pure Java OOP, ACID JDBC transactions, and PL/SQL stored procedures. " +
                    "Discover 32 syllabi volumes across 75+ physical barcode copies.\n")
            .append("      </p>\n")
            .append("      <div class=\"hero-actions-row\">\n")
            .append("        <a href=\"/login\" class=\"btn-pill-primary\">&#128273; Enter Library Workspace &rarr;</a>\n")
            .append("        <a href=\"/register\" class=\"btn-pill-glass\">&#127891; Student Registration</a>\n")
            .append("      </div>\n")
            .append("      <!-- Continuous Frosted Stats Ribbon (Non-boxy) -->\n")
            .append("      <div class=\"campus-stats-ribbon\">\n")
            .append("        <div class=\"stat-ribbon-item\">\n")
            .append("          <div class=\"stat-ribbon-num\">32</div>\n")
            .append("          <div class=\"stat-ribbon-lbl\">Syllabi Titles</div>\n")
            .append("        </div>\n")
            .append("        <div class=\"stat-ribbon-divider\"></div>\n")
            .append("        <div class=\"stat-ribbon-item\">\n")
            .append("          <div class=\"stat-ribbon-num\">75+</div>\n")
            .append("          <div class=\"stat-ribbon-lbl\">Copies</div>\n")
            .append("        </div>\n")
            .append("        <div class=\"stat-ribbon-divider\"></div>\n")
            .append("        <div class=\"stat-ribbon-item\">\n")
            .append("          <div class=\"stat-ribbon-num\">14d</div>\n")
            .append("          <div class=\"stat-ribbon-lbl\">Loan Cycle</div>\n")
            .append("        </div>\n")
            .append("        <div class=\"stat-ribbon-divider\"></div>\n")
            .append("        <div class=\"stat-ribbon-item\">\n")
            .append("          <div class=\"stat-ribbon-num\">10</div>\n")
            .append("          <div class=\"stat-ribbon-lbl\">SQL Reports</div>\n")
            .append("        </div>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("    <!-- Right Column: Tactile Book Spotlight Card -->\n")
            .append("    <div>\n")
            .append("      <div class=\"spotlight-book-card\">\n")
            .append("        <div class=\"spotlight-header\">\n")
            .append("          <span class=\"spotlight-pill\">Featured Volume</span>\n")
            .append("          <span class=\"badge badge-success\" style=\"font-size: 0.75rem;\">3 Copies In Stock</span>\n")
            .append("        </div>\n")
            .append("        <div class=\"spotlight-cover\">\n")
            .append("          <div class=\"spotlight-book-title\">Distributed Systems</div>\n")
            .append("          <div class=\"spotlight-book-author\">Principles and Paradigms &bull; Andrew S. Tanenbaum</div>\n")
            .append("        </div>\n")
            .append("        <div class=\"spotlight-meta-grid\">\n")
            .append("          <div class=\"spotlight-meta-item\">\n")
            .append("            <div class=\"spotlight-meta-label\">Accession Barcode</div>\n")
            .append("            <div class=\"spotlight-meta-val\"><code>ACC-10001</code></div>\n")
            .append("          </div>\n")
            .append("          <div class=\"spotlight-meta-item\">\n")
            .append("            <div class=\"spotlight-meta-label\">Shelf Location</div>\n")
            .append("            <div class=\"spotlight-meta-val\">Shelf CS-A1</div>\n")
            .append("          </div>\n")
            .append("          <div class=\"spotlight-meta-item\">\n")
            .append("            <div class=\"spotlight-meta-label\">Standard Loan</div>\n")
            .append("            <div class=\"spotlight-meta-val\">14 Days (INR 5/d)</div>\n")
            .append("          </div>\n")
            .append("          <div class=\"spotlight-meta-item\">\n")
            .append("            <div class=\"spotlight-meta-label\">Discipline</div>\n")
            .append("            <div class=\"spotlight-meta-val\">Computer Science</div>\n")
            .append("          </div>\n")
            .append("        </div>\n")
            .append("        <a href=\"/login\" class=\"btn btn-primary btn-block\" style=\"border-radius: var(--radius-full); padding: 0.75rem 1.25rem;\">Sign In to Borrow Volume &rarr;</a>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("</section>\n");

        // =========================================================================
        // ROLE HIGHLIGHTS: Fast Evaluation Credentials for Project Review
        // =========================================================================
        html.append("<!-- ========================================================================== -->\n")
            .append("<!-- ROLE HIGHLIGHTS: Fast Evaluation Credentials for Project Review           -->\n")
            .append("<!-- ========================================================================== -->\n")
            .append("<section style=\"padding: 4.5rem 2rem 2rem; max-width: 1200px; margin: 0 auto; text-align: center;\">\n")
            .append("  <span class=\"badge badge-neutral\" style=\"font-size: 0.85rem; padding: 0.4rem 1rem; margin-bottom: 0.75rem;\">ONE-CLICK EVALUATION ROLES</span>\n")
            .append("  <h2 style=\"font-size: 2.2rem; font-weight: 800; color: #0b0f19; margin-bottom: 0.75rem;\">Role-Based Access Control Portals</h2>\n")
            .append("  <p style=\"color: var(--text-muted); max-width: 700px; margin: 0 auto 2.5rem; font-size: 1rem;\">Each role provides customized access boundaries enforced through SHA-256 session tokens and Java HTTP request dispatchers.</p>\n")
            .append("  <div class=\"role-showcase-grid\">\n")
            .append("    <div class=\"role-card-item\">\n")
            .append("      <div class=\"role-card-header\">\n")
            .append("        <div class=\"role-icon-box\">&#128081;</div>\n")
            .append("        <div><div class=\"role-title-text\">Administrator</div><span class=\"badge badge-danger\" style=\"font-size: 0.75rem;\">Full System Control</span></div>\n")
            .append("      </div>\n")
            .append("      <p class=\"role-desc-text\">Manage librarians and student accounts, view immutable audit logs, inspect SQL reports, and monitor system health.</p>\n")
            .append("      <div class=\"role-cred-box\">\n")
            .append("        <div><strong>Demo Email:</strong> <code>admin@library.edu</code></div>\n")
            .append("        <div><strong>Password:</strong> <code>admin123</code></div>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("    <div class=\"role-card-item role-librarian\">\n")
            .append("      <div class=\"role-card-header\">\n")
            .append("        <div class=\"role-icon-box\">&#128214;</div>\n")
            .append("        <div><div class=\"role-title-text\">Staff Librarian</div><span class=\"badge badge-primary\" style=\"font-size: 0.75rem;\">Circulation Desk</span></div>\n")
            .append("      </div>\n")
            .append("      <p class=\"role-desc-text\">Issue books, process returns with automated late fine calculation, collect fees, manage physical copies, and restock catalog.</p>\n")
            .append("      <div class=\"role-cred-box\">\n")
            .append("        <div><strong>Demo Email:</strong> <code>librarian@library.edu</code></div>\n")
            .append("        <div><strong>Password:</strong> <code>librarian123</code></div>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("    <div class=\"role-card-item role-student\">\n")
            .append("      <div class=\"role-card-header\">\n")
            .append("        <div class=\"role-icon-box\">&#127891;</div>\n")
            .append("        <div><div class=\"role-title-text\">Enrolled Student</div><span class=\"badge badge-success\" style=\"font-size: 0.75rem;\">Self-Service Portal</span></div>\n")
            .append("      </div>\n")
            .append("      <p class=\"role-desc-text\">Search catalog, 1-click self-borrow available books, return borrowed items, place reservations in waitlist, and view fines.</p>\n")
            .append("      <div class=\"role-cred-box\">\n")
            .append("        <div><strong>Demo Email:</strong> <code>aarav.patel@student.edu</code></div>\n")
            .append("        <div><strong>Password:</strong> <code>student123</code></div>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("</section>\n");

        // =========================================================================
        // CORE ENTERPRISE SYSTEM MODULES
        // =========================================================================
        html.append("<!-- ========================================================================== -->\n")
            .append("<!-- CORE ENTERPRISE SYSTEM MODULES (4-Member Architecture Division)            -->\n")
            .append("<!-- ========================================================================== -->\n")
            .append("<section style=\"padding: 3rem 2rem 2rem; max-width: 1200px; margin: 0 auto; text-align: center;\">\n")
            .append("  <span class=\"badge badge-neutral\" style=\"font-size: 0.85rem; padding: 0.4rem 1rem; margin-bottom: 0.75rem;\">SYSTEM HIGHLIGHTS</span>\n")
            .append("  <h2 style=\"font-size: 2.2rem; font-weight: 800; color: #0b0f19; margin-bottom: 0.75rem;\">Engineered for Academic Excellence</h2>\n")
            .append("  <p style=\"color: var(--text-muted); max-width: 720px; margin: 0 auto; font-size: 1rem;\">Built strictly conforming to semester requirements: zero JavaScript frameworks, pure HTML5/CSS3, and enterprise Java JDBC.</p>\n")
            .append("  <div class=\"feature-grid\">\n")
            .append("    <div class=\"feature-card\">\n")
            .append("      <div class=\"feature-icon\">&#128218;</div>\n")
            .append("      <h3>Circulation Desk &amp; Self-Borrow</h3>\n")
            .append("      <p>Automated 14-day book loans, barcode copy allocation, return processing, and real-time inventory count restock.</p>\n")
            .append("    </div>\n")
            .append("    <div class=\"feature-card\">\n")
            .append("      <div class=\"feature-icon\">&#127991;</div>\n")
            .append("      <h3>Physical Accession Tracking</h3>\n")
            .append("      <p>Every physical book copy maintains a unique accession barcode number, condition grade, and specific shelf location.</p>\n")
            .append("    </div>\n")
            .append("    <div class=\"feature-card\">\n")
            .append("      <div class=\"feature-icon\">&#9878;</div>\n")
            .append("      <h3>Automated Fine Computation</h3>\n")
            .append("      <p>Instant fine calculation at INR 5.00/day for overdue loans. Supports fee settlement receipts and blocks borrowing if dues exist.</p>\n")
            .append("    </div>\n")
            .append("    <div class=\"feature-card\">\n")
            .append("      <div class=\"feature-icon\">&#128339;</div>\n")
            .append("      <h3>Reservation Queue Waitlist</h3>\n")
            .append("      <p>When copies are out of stock, students enter an automated FIFO reservation queue with notification triggers upon return.</p>\n")
            .append("    </div>\n")
            .append("    <div class=\"feature-card\">\n")
            .append("      <div class=\"feature-icon\">&#128202;</div>\n")
            .append("      <h3>10 Analytical SQL Reports</h3>\n")
            .append("      <p>Complex relational queries using SQL JOINs, GROUP BY, HAVING, subqueries, and views for faculty review.</p>\n")
            .append("    </div>\n")
            .append("    <div class=\"feature-card\">\n")
            .append("      <div class=\"feature-icon\">&#128272;</div>\n")
            .append("      <h3>ACID Transactions &amp; Security</h3>\n")
            .append("      <p>Atomic database commit/rollback on every loan, SHA-256 password security, and complete audit log records.</p>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("</section>\n");

        // =========================================================================
        // CAMPUS CIRCULATION POLICY & TIMINGS BANNER
        // =========================================================================
        html.append("<!-- ========================================================================== -->\n")
            .append("<!-- CAMPUS CIRCULATION GUIDELINES & LIBRARY TIMINGS BANNER                     -->\n")
            .append("<!-- ========================================================================== -->\n")
            .append("<div class=\"policy-banner\">\n")
            .append("  <div class=\"policy-icon-col\">&#128220;</div>\n")
            .append("  <div class=\"policy-content-col\">\n")
            .append("    <h3>Campus Library Circulation Guidelines</h3>\n")
            .append("    <p>All active library members must adhere to semester circulation rules. Borrowing privileges are automatically synchronized with fee clearance.</p>\n")
            .append("    <div class=\"policy-chips\">\n")
            .append("      <span class=\"policy-chip-item\">&#128337; Hours: Mon - Sat (8:00 AM - 9:00 PM)</span>\n")
            .append("      <span class=\"policy-chip-item\">&#128218; Loan Limit: Max 3 Books / Student</span>\n")
            .append("      <span class=\"policy-chip-item\">&#128197; Standard Loan Period: 14 Days</span>\n")
            .append("      <span class=\"policy-chip-item\">&#9888; Late Fine Rate: INR 5.00 / Day</span>\n")
            .append("      <span class=\"policy-chip-item\">&#128179; Reference Books: In-Library Use Only</span>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        // =========================================================================
        // INSTITUTIONAL CAMPUS FOOTER
        // =========================================================================
        html.append("<!-- ========================================================================== -->\n")
            .append("<!-- INSTITUTIONAL FOOTER & 4-MEMBER PROJECT COMPLIANCE                         -->\n")
            .append("<!-- ========================================================================== -->\n")
            .append("<footer class=\"campus-footer\">\n")
            .append("  <div class=\"footer-inner\">\n")
            .append("    <div class=\"footer-col\">\n")
            .append("      <h4>Central Campus Library</h4>\n")
            .append("      <p>Smart Library Management System (SLMS) is an enterprise academic circulation and cataloging platform engineered for university departments.</p>\n")
            .append("    </div>\n")
            .append("    <div class=\"footer-col\">\n")
            .append("      <h4>Semester Project Division</h4>\n")
            .append("      <ul>\n")
            .append("        <li><strong>Member 1:</strong> Auth &amp; Student Portal</li>\n")
            .append("        <li><strong>Member 2:</strong> Catalog &amp; Librarian Desk</li>\n")
            .append("        <li><strong>Member 3:</strong> SQL Schema &amp; PL/SQL Routines</li>\n")
            .append("        <li><strong>Member 4:</strong> Fines, Reservations &amp; Reports</li>\n")
            .append("      </ul>\n")
            .append("    </div>\n")
            .append("    <div class=\"footer-col\">\n")
            .append("      <h4>Strict Technology Compliance</h4>\n")
            .append("      <ul>\n")
            .append("        <li>&#10003; 100% Pure HTML5 &amp; CSS3 (No Frameworks)</li>\n")
            .append("        <li>&#10003; Java SE 17 Object-Oriented Backend</li>\n")
            .append("        <li>&#10003; JDBC PreparedStatements &amp; ACID Transactions</li>\n")
            .append("        <li>&#10003; SQL/PL-SQL Procedures, Functions &amp; Triggers</li>\n")
            .append("      </ul>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("  <div class=\"footer-bottom-bar\">\n")
            .append("    <span>&copy; 2026 Smart Library Management System &bull; College Semester Project</span>\n")
            .append("    <span>Department of Computer Science &amp; Engineering</span>\n")
            .append("  </div>\n")
            .append("</footer>\n");

        return HtmlRenderer.renderPublicPage("Campus Central Library", "home", html.toString());
    }

    public String renderAbout() {
        String content =
            "<div class=\"hero-section\" style=\"text-align: left; max-width: 950px;\">\n" +
            "  <h1 class=\"hero-title\" style=\"font-size: 2.2rem;\">About Smart Library Management System</h1>\n" +
            "  <p class=\"hero-subtitle\" style=\"margin-left: 0;\">\n" +
            "    Engineered as an advanced 4-member semester college project demonstrating database normalization, " +
            "    relational integrity, PL/SQL business routines, and clean Java OOP with JDBC.\n" +
            "  </p>\n" +
            "  <div class=\"card-table\" style=\"padding: 2rem; margin-top: 2rem;\">\n" +
            "    <h2 style=\"margin-bottom: 1rem; color: var(--primary);\">4-Member Project Division</h2>\n" +
            "    <ul style=\"line-height: 2; margin-left: 1.5rem; color: var(--text-main);\">\n" +
            "      <li><strong>Member 1: Authentication &amp; Student Module</strong> &mdash; User sessions, student registration, profile management, and role-based access control.</li>\n" +
            "      <li><strong>Member 2: Book Catalog &amp; Librarian Module</strong> &mdash; Book management, physical copy accession tracking, issue processing, and return workflows.</li>\n" +
            "      <li><strong>Member 3: Database &amp; SQL / PL-SQL Architecture</strong> &mdash; Relational schema normalization (3NF), JDBC connection pooling, PL/SQL procedures, and triggers.</li>\n" +
            "      <li><strong>Member 4: Reservations, Fines &amp; Analytical Reports</strong> &mdash; Out-of-stock reservation queue, overdue fine computation, and 10 SQL analytical reports.</li>\n" +
            "    </ul>\n" +
            "  </div>\n" +
            "  <div class=\"card-table\" style=\"padding: 2rem; margin-top: 2rem;\">\n" +
            "    <h2 style=\"margin-bottom: 1rem; color: var(--primary);\">Architectural Compliance</h2>\n" +
            "    <p style=\"margin-bottom: 1rem;\">This project strictly follows the required pure technology stack:</p>\n" +
            "    <div class=\"hero-badges\" style=\"justify-content: flex-start; margin-bottom: 1rem;\">\n" +
            "      <span class=\"tech-tag\">HTML5 (Clean Semantic Markup)</span>\n" +
            "      <span class=\"tech-tag\">CSS3 (Zero Frameworks)</span>\n" +
            "      <span class=\"tech-tag\">Java SE 17 Backend</span>\n" +
            "      <span class=\"tech-tag\">JDBC PreparedStatements</span>\n" +
            "      <span class=\"tech-tag\">ACID Transactions</span>\n" +
            "      <span class=\"tech-tag\">PL/SQL Procedures &amp; Triggers</span>\n" +
            "    </div>\n" +
            "    <p>Zero external JavaScript frameworks or Node/Python dependencies were used.</p>\n" +
            "  </div>\n" +
            "</div>";

        return HtmlRenderer.renderPublicPage("About", "about", content);
    }

    public String renderLogin(String flashSuccess, String flashError) {
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"auth-page-bg\">\n")
            .append("  <div class=\"ambient-glow glow-1\"></div>\n")
            .append("  <div class=\"ambient-glow glow-2\"></div>\n")
            .append("  <div class=\"auth-card animate-slide-up\">\n")
            .append("    <div class=\"auth-card-top\">\n")
            .append("      <a href=\"/\" class=\"back-home-link\">&larr; Back to Home</a>\n")
            .append("    </div>\n")
            .append("    <div class=\"auth-header\">\n")
            .append("      <div class=\"auth-logo-badge\">\n")
            .append("        <span class=\"logo-icon\">&#128218;</span>\n")
            .append("        <span class=\"logo-text\">SLMS</span>\n")
            .append("      </div>\n")
            .append("      <h1 class=\"auth-title\">Smart Library Portal</h1>\n")
            .append("      <p class=\"auth-subtitle\">Secure sign in for Students, Librarians, and Administrators</p>\n")
            .append("    </div>\n");

        if (flashSuccess != null && !flashSuccess.isEmpty()) {
            content.append("    <div class=\"alert alert-success animate-fade-in\"><span>&#10003; ").append(HtmlRenderer.escape(flashSuccess)).append("</span></div>\n");
        }
        if (flashError != null && !flashError.isEmpty()) {
            content.append("    <div class=\"alert alert-danger animate-fade-in\"><span>&#9888; ").append(HtmlRenderer.escape(flashError)).append("</span></div>\n");
        }

        content.append("    <form method=\"POST\" action=\"/login\" class=\"auth-form\">\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\" for=\"email\">College Email Address</label>\n")
            .append("        <div class=\"input-wrapper\">\n")
            .append("          <input type=\"email\" id=\"email\" name=\"email\" class=\"form-input\" required placeholder=\"e.g. admin@library.edu\">\n")
            .append("        </div>\n")
            .append("      </div>\n")
            .append("      <div class=\"form-group\">\n")
            .append("        <label class=\"form-label\" for=\"password\">Password</label>\n")
            .append("        <div class=\"input-wrapper\">\n")
            .append("          <input type=\"password\" id=\"password\" name=\"password\" class=\"form-input\" required placeholder=\"Enter your password\">\n")
            .append("        </div>\n")
            .append("      </div>\n")
            .append("      <div class=\"auth-actions\">\n")
            .append("        <button type=\"submit\" class=\"btn btn-primary btn-block btn-glow\">Sign In to Workspace &rarr;</button>\n")
            .append("      </div>\n")
            .append("    </form>\n")
            .append("    <div class=\"demo-credentials-box\">\n")
            .append("      <div class=\"demo-title\">Quick 1-Click Evaluation Sign In:</div>\n")
            .append("      <div class=\"demo-chips-grid\">\n")
            .append("        <form method=\"POST\" action=\"/login\" style=\"margin:0;\">\n")
            .append("          <input type=\"hidden\" name=\"email\" value=\"admin@library.edu\">\n")
            .append("          <input type=\"hidden\" name=\"password\" value=\"admin123\">\n")
            .append("          <button type=\"submit\" class=\"demo-chip chip-admin\">&#128081; Admin</button>\n")
            .append("        </form>\n")
            .append("        <form method=\"POST\" action=\"/login\" style=\"margin:0;\">\n")
            .append("          <input type=\"hidden\" name=\"email\" value=\"librarian@library.edu\">\n")
            .append("          <input type=\"hidden\" name=\"password\" value=\"librarian123\">\n")
            .append("          <button type=\"submit\" class=\"demo-chip chip-librarian\">&#128214; Librarian</button>\n")
            .append("        </form>\n")
            .append("        <form method=\"POST\" action=\"/login\" style=\"margin:0;\">\n")
            .append("          <input type=\"hidden\" name=\"email\" value=\"aarav.patel@student.edu\">\n")
            .append("          <input type=\"hidden\" name=\"password\" value=\"student123\">\n")
            .append("          <button type=\"submit\" class=\"demo-chip chip-student\">&#127891; Student</button>\n")
            .append("        </form>\n")
            .append("      </div>\n")
            .append("    </div>\n")
            .append("    <div class=\"auth-footer\">\n")
            .append("      Enrolling student? <a href=\"/register\" class=\"auth-link\">Create an account &rarr;</a>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        return HtmlRenderer.renderAuthPage("Sign In", content.toString());
    }

    public String renderRegister(String flashError) {
        StringBuilder content = new StringBuilder();
        content.append("<div class=\"auth-page-bg\">\n")
            .append("  <div class=\"ambient-glow glow-1\"></div>\n")
            .append("  <div class=\"ambient-glow glow-2\"></div>\n")
            .append("  <div class=\"auth-card auth-card-wide animate-slide-up\">\n")
            .append("    <div class=\"auth-card-top\">\n")
            .append("      <a href=\"/login\" class=\"back-home-link\">&larr; Back to Sign In</a>\n")
            .append("    </div>\n")
            .append("    <div class=\"auth-header\">\n")
            .append("      <div class=\"auth-logo-badge\">\n")
            .append("        <span class=\"logo-icon\">&#127891;</span>\n")
            .append("        <span class=\"logo-text\">SLMS</span>\n")
            .append("      </div>\n")
            .append("      <h1 class=\"auth-title\">Student Registration</h1>\n")
            .append("      <p class=\"auth-subtitle\">Create your verified digital library membership account</p>\n")
            .append("    </div>\n");

        if (flashError != null && !flashError.isEmpty()) {
            content.append("    <div class=\"alert alert-danger animate-fade-in\"><span>&#9888; ").append(HtmlRenderer.escape(flashError)).append("</span></div>\n");
        }

        content.append("    <form method=\"POST\" action=\"/register\" class=\"auth-form\">\n")
            .append("      <div class=\"form-grid\">\n")
            .append("        <div class=\"form-group full-width\">\n")
            .append("          <label class=\"form-label\" for=\"name\">Full Name *</label>\n")
            .append("          <input type=\"text\" id=\"name\" name=\"name\" class=\"form-input\" required placeholder=\"e.g. John Doe\">\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group\">\n")
            .append("          <label class=\"form-label\" for=\"email\">College Email *</label>\n")
            .append("          <input type=\"email\" id=\"email\" name=\"email\" class=\"form-input\" required placeholder=\"e.g. john@student.edu\">\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group\">\n")
            .append("          <label class=\"form-label\" for=\"phone\">Phone Number</label>\n")
            .append("          <input type=\"text\" id=\"phone\" name=\"phone\" class=\"form-input\" placeholder=\"e.g. 9876543210\">\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group\">\n")
            .append("          <label class=\"form-label\" for=\"registerNumber\">Register Number *</label>\n")
            .append("          <input type=\"text\" id=\"registerNumber\" name=\"registerNumber\" class=\"form-input\" required placeholder=\"e.g. 24CS101\">\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group\">\n")
            .append("          <label class=\"form-label\" for=\"department\">Department *</label>\n")
            .append("          <input type=\"text\" id=\"department\" name=\"department\" class=\"form-input\" required placeholder=\"e.g. Computer Science\">\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group\">\n")
            .append("          <label class=\"form-label\" for=\"year\">Year of Study *</label>\n")
            .append("          <select id=\"year\" name=\"year\" class=\"form-select\" required>\n")
            .append("            <option value=\"1\">1st Year</option>\n")
            .append("            <option value=\"2\">2nd Year</option>\n")
            .append("            <option value=\"3\" selected>3rd Year</option>\n")
            .append("            <option value=\"4\">4th Year</option>\n")
            .append("            <option value=\"5\">5th Year</option>\n")
            .append("          </select>\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group\">\n")
            .append("          <label class=\"form-label\" for=\"section\">Section *</label>\n")
            .append("          <input type=\"text\" id=\"section\" name=\"section\" class=\"form-input\" required value=\"A\" placeholder=\"A, B, or C\">\n")
            .append("        </div>\n")
            .append("        <div class=\"form-group full-width\">\n")
            .append("          <label class=\"form-label\" for=\"password\">Account Password *</label>\n")
            .append("          <input type=\"password\" id=\"password\" name=\"password\" class=\"form-input\" required placeholder=\"Minimum 6 characters\">\n")
            .append("        </div>\n")
            .append("      </div>\n")
            .append("      <div class=\"auth-actions\" style=\"margin-top: 2rem;\">\n")
            .append("        <button type=\"submit\" class=\"btn btn-primary btn-block btn-glow\">Complete Student Registration &rarr;</button>\n")
            .append("      </div>\n")
            .append("    </form>\n")
            .append("    <div class=\"auth-footer\">\n")
            .append("      Already have an account? <a href=\"/login\" class=\"auth-link\">Sign in here &rarr;</a>\n")
            .append("    </div>\n")
            .append("  </div>\n")
            .append("</div>\n");

        return HtmlRenderer.renderAuthPage("Student Registration", content.toString());
    }
}
