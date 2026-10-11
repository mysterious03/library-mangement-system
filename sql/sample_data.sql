-- ============================================================================
-- SMART LIBRARY MANAGEMENT SYSTEM - SAMPLE DATASET
-- 30+ Books, 12 Students, 2 Librarians, 1 Admin, 6 Authors, 6 Categories,
-- 5 Publishers, 75+ Book Copies, Issues, Returns, Reservations, and Fines.
-- ============================================================================

-- SHA-256 Passwords:
-- 'admin123'     -> 240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9
-- 'librarian123' -> 548b1d9bf59c0490b4bfb563606f527c244ad064431945f34086b971239c4f14
-- 'student123'   -> 6f1c4e1b8b201a4e104e7b1a774fcf2f495b292e01dfd29486c9d3ef64fc609d

-- ----------------------------------------------------------------------------
-- SYSTEM SETTINGS
-- ----------------------------------------------------------------------------
INSERT INTO SYSTEM_SETTINGS (setting_key, setting_value, description) VALUES
('DAILY_FINE_RATE', '5.0', 'Daily fine in INR for each overdue day'),
('MAX_BORROW_LIMIT', '3', 'Maximum number of books a student can borrow concurrently'),
('LOAN_PERIOD_DAYS', '14', 'Default loan duration in days');

-- ----------------------------------------------------------------------------
-- USERS (Admin, Librarians, Students)
-- ----------------------------------------------------------------------------
-- 1. Admin
INSERT INTO USERS (user_id, name, email, password, phone, role, status) VALUES
(1, 'Dr. Rajesh Sharma', 'admin@library.edu', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '9876543210', 'ADMIN', 'ACTIVE');

-- 2-3. Librarians
INSERT INTO USERS (user_id, name, email, password, phone, role, status) VALUES
(2, 'Mrs. Ananya Sen', 'librarian@library.edu', 'ab8e89c55367f55a2f933b8dc8a9994d61f997df2b402274eb943fa22d77394a', '9876543211', 'LIBRARIAN', 'ACTIVE'),
(3, 'Mr. Vikram Verma', 'vikram@library.edu', 'ab8e89c55367f55a2f933b8dc8a9994d61f997df2b402274eb943fa22d77394a', '9876543212', 'LIBRARIAN', 'ACTIVE');

-- 4-15. Students (12 Students)
INSERT INTO USERS (user_id, name, email, password, phone, role, status) VALUES
(4, 'Aarav Patel', 'aarav.patel@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456701', 'STUDENT', 'ACTIVE'),
(5, 'Diya Iyer', 'diya.iyer@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456702', 'STUDENT', 'ACTIVE'),
(6, 'Rohan Kulkarni', 'rohan.k@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456703', 'STUDENT', 'ACTIVE'),
(7, 'Sneha Reddy', 'sneha.reddy@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456704', 'STUDENT', 'ACTIVE'),
(8, 'Kabir Mehta', 'kabir.mehta@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456705', 'STUDENT', 'ACTIVE'),
(9, 'Ananya Roy', 'ananya.roy@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456706', 'STUDENT', 'ACTIVE'),
(10, 'Tanmay Joshi', 'tanmay.j@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456707', 'STUDENT', 'ACTIVE'),
(11, 'Pooja Nair', 'pooja.nair@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456708', 'STUDENT', 'ACTIVE'),
(12, 'Aditya Choudhury', 'aditya.c@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456709', 'STUDENT', 'ACTIVE'),
(13, 'Meera Deshmukh', 'meera.d@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456710', 'STUDENT', 'ACTIVE'),
(14, 'Varun Bhatia', 'varun.b@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456711', 'STUDENT', 'ACTIVE'),
(15, 'Ishaan Gupta', 'ishaan.g@student.edu', '703b0a3d6ad75b649a28adde7d83c6251da457549263bc7ff45ec709b0a8448b', '9123456712', 'STUDENT', 'ACTIVE');

-- ----------------------------------------------------------------------------
-- LIBRARIANS
-- ----------------------------------------------------------------------------
INSERT INTO LIBRARIANS (librarian_id, user_id, employee_id) VALUES
(1, 2, 'LIB-2021-01'),
(2, 3, 'LIB-2022-04');

-- ----------------------------------------------------------------------------
-- STUDENTS (12 Students)
-- ----------------------------------------------------------------------------
INSERT INTO STUDENTS (student_id, user_id, register_number, department, year, section) VALUES
(1, 4, '21CS001', 'Computer Science and Engineering', 3, 'A'),
(2, 5, '21CS042', 'Computer Science and Engineering', 3, 'B'),
(3, 6, '22IT015', 'Information Technology', 2, 'A'),
(4, 7, '22IT033', 'Information Technology', 2, 'B'),
(5, 8, '20EC019', 'Electronics and Communication', 4, 'A'),
(6, 9, '20EC051', 'Electronics and Communication', 4, 'B'),
(7, 10, '21ME008', 'Mechanical Engineering', 3, 'A'),
(8, 11, '23CS005', 'Computer Science and Engineering', 1, 'A'),
(9, 12, '23CS077', 'Computer Science and Engineering', 1, 'B'),
(10, 13, '22EE021', 'Electrical Engineering', 2, 'A'),
(11, 14, '21CE012', 'Civil Engineering', 3, 'A'),
(12, 15, '23AI002', 'Artificial Intelligence & Data Science', 1, 'A');

-- ----------------------------------------------------------------------------
-- AUTHORS (6 Authors)
-- ----------------------------------------------------------------------------
INSERT INTO AUTHORS (author_id, author_name, biography) VALUES
(1, 'Robert C. Martin', 'Renowned software engineer known as Uncle Bob, author of Clean Code and agile manifesto signatory.'),
(2, 'Thomas H. Cormen', 'Professor of Computer Science and co-author of the definitive textbook Introduction to Algorithms (CLRS).'),
(3, 'Abraham Silberschatz', 'Professor of Computer Science at Yale University, widely known for Database System Concepts.'),
(4, 'Andrew S. Tanenbaum', 'Professor Emeritus at Vrije Universiteit Amsterdam, famous for Operating Systems & Computer Networks.'),
(5, 'Joshua Bloch', 'Former Chief Java Architect at Google and author of Effective Java.'),
(6, 'Donald E. Knuth', 'Legendary computer scientist, mathematician, and author of The Art of Computer Programming.');

-- ----------------------------------------------------------------------------
-- CATEGORIES (6 Categories)
-- ----------------------------------------------------------------------------
INSERT INTO CATEGORIES (category_id, category_name, description) VALUES
(1, 'Computer Science', 'Core computing, algorithms, programming paradigms, and software systems.'),
(2, 'Database Systems', 'Relational database management, SQL, distributed data, and storage architecture.'),
(3, 'Networking & Security', 'Telecommunications, protocol stacks, cybersecurity, and cloud networking.'),
(4, 'Artificial Intelligence', 'Machine learning, deep neural networks, natural language processing, and robotics.'),
(5, 'Software Engineering', 'System architecture, design patterns, clean design, and agile methodologies.'),
(6, 'General Science & Mathematics', 'Discrete mathematics, linear algebra, statistics, and applied sciences.');

-- ----------------------------------------------------------------------------
-- PUBLISHERS (5 Publishers)
-- ----------------------------------------------------------------------------
INSERT INTO PUBLISHERS (publisher_id, publisher_name, address, contact) VALUES
(1, 'Pearson Education', 'Upper Saddle River, New Jersey, USA', 'contact@pearson.com'),
(2, 'MIT Press', 'Cambridge, Massachusetts, USA', 'info@mitpress.edu'),
(3, 'McGraw-Hill Education', 'New York, NY, USA', 'support@mheducation.com'),
(4, 'O Reilly Media', 'Sebastopol, California, USA', 'orders@oreilly.com'),
(5, 'Addison-Wesley Professional', 'Boston, Massachusetts, USA', 'inquiries@addison-wesley.com');

-- ----------------------------------------------------------------------------
-- BOOKS (32 Books)
-- ----------------------------------------------------------------------------
INSERT INTO BOOKS (book_id, isbn, title, author_id, category_id, publisher_id, publication_year, edition, language, description) VALUES
(1, '9780132350884', 'Clean Code: A Handbook of Agile Software Craftsmanship', 1, 5, 1, 2008, '1st Edition', 'English', 'A comprehensive guide on writing readable, reusable, and maintainable software code.'),
(2, '9780262033848', 'Introduction to Algorithms (CLRS)', 2, 1, 2, 2009, '3rd Edition', 'English', 'The comprehensive and rigorous benchmark textbook covering all modern algorithms and data structures.'),
(3, '9780073523323', 'Database System Concepts', 3, 2, 3, 2019, '7th Edition', 'English', 'The foundational textbook for relational algebra, SQL, concurrency control, and query optimization.'),
(4, '9780132126953', 'Computer Networks', 4, 3, 1, 2011, '5th Edition', 'English', 'In-depth treatment of networking hardware, protocol stacks, wireless transmissions, and cybersecurity.'),
(5, '9780134685991', 'Effective Java', 5, 1, 5, 2017, '3rd Edition', 'English', 'Essential best practices for the Java platform covering generics, lambdas, streams, and concurrency.'),
(6, '9780201896831', 'The Art of Computer Programming: Vol 1', 6, 1, 5, 1997, '3rd Edition', 'English', 'Fundamental algorithms, information structures, and mathematical analysis of programming.'),
(7, '9780134494166', 'Clean Architecture: A Craftsman Guide', 1, 5, 1, 2017, '1st Edition', 'English', 'Universal rules of software architecture focusing on boundaries, decoupling, and high-level structure.'),
(8, '9780130340740', 'Operating Systems: Design and Implementation', 4, 1, 1, 2006, '3rd Edition', 'English', 'Principles and code implementation of modern operating systems based on MINIX 3.'),
(9, '9780072821871', 'Operating System Concepts', 3, 1, 3, 2018, '10th Edition', 'English', 'Comprehensive coverage of OS processes, memory management, virtualization, and file systems.'),
(10, '9780131429383', 'Modern Operating Systems', 4, 1, 1, 2014, '4th Edition', 'English', 'Covers virtualization, multi-core processors, real-time operating systems, and kernel security.'),
(11, '9780132350891', 'The Clean Coder: Code of Conduct for Developers', 1, 5, 1, 2011, '1st Edition', 'English', 'Professional standards, attitude, estimation, refactoring, and ethics in software engineering.'),
(12, '9781449373320', 'Designing Data-Intensive Applications', 3, 2, 4, 2017, '1st Edition', 'English', 'The big ideas behind reliable, scalable, and maintainable data storage systems and distributed systems.'),
(13, '9780321125217', 'Domain-Driven Design', 1, 5, 5, 2003, '1st Edition', 'English', 'Tackling complexity in the heart of software through domain modeling and ubiquitous language.'),
(14, '9780201633610', 'Design Patterns: Elements of Reusable Object-Oriented Software', 1, 5, 5, 1994, '1st Edition', 'English', 'The classic Gang of Four text introducing creational, structural, and behavioral design patterns.'),
(15, '9780131103627', 'The C Programming Language', 6, 1, 1, 1988, '2nd Edition', 'English', 'The authoritative guide to the C programming language by Brian Kernighan and Dennis Ritchie.'),
(16, '9780596007126', 'Head First Design Patterns', 1, 5, 4, 2004, '1st Edition', 'English', 'A brain-friendly visual guide to object-oriented design principles and patterns.'),
(17, '9781491950357', 'Building Microservices', 4, 5, 4, 2015, '1st Edition', 'English', 'Designing fine-grained systems, service boundaries, inter-service communication, and resilience.'),
(18, '9780134092669', 'Computer Organization and Architecture', 4, 1, 1, 2015, '10th Edition', 'English', 'Instruction set design, pipelining, memory hierarchy, cache coherency, and parallel computing.'),
(19, '9780132143011', 'Artificial Intelligence: A Modern Approach', 2, 4, 1, 2020, '4th Edition', 'English', 'The leading textbook in AI covering probabilistic reasoning, search algorithms, and machine learning.'),
(20, '9780262035613', 'Deep Learning', 2, 4, 2, 2016, '1st Edition', 'English', 'Comprehensive mathematical foundation for deep feedforward nets, convolutional networks, and RNNs.'),
(21, '9781491957660', 'Hands-On Machine Learning with Scikit-Learn and TensorFlow', 2, 4, 4, 2019, '2nd Edition', 'English', 'Practical concrete machine learning, linear models, gradient boosting, and deep neural architectures.'),
(22, '9780262039291', 'Reinforcement Learning: An Introduction', 2, 4, 2, 2018, '2nd Edition', 'English', 'Markov decision processes, dynamic programming, temporal-difference learning, and policy gradients.'),
(23, '9781449331818', 'Learning Python', 5, 1, 4, 2013, '5th Edition', 'English', 'Comprehensive, in-depth tutorial to the Python language fundamentals and object-oriented syntax.'),
(24, '9780321356680', 'Effective C++', 5, 1, 5, 2005, '3rd Edition', 'English', '55 specific ways to improve your programs and designs using C++ idiomatically.'),
(25, '9780137081899', 'Database Systems: The Complete Book', 3, 2, 1, 2008, '2nd Edition', 'English', 'Exhaustive reference on database modeling, SQL, index tuning, transaction isolation, and XML.'),
(26, '9781449361327', 'High Performance MySQL', 3, 2, 4, 2012, '3rd Edition', 'English', 'Optimization, backups, replication, sharding, and high-availability database clustering.'),
(27, '9780262032933', 'Introduction to the Theory of Computation', 6, 6, 2, 2012, '3rd Edition', 'English', 'Automata theory, formal languages, computability, Turing machines, and NP-completeness.'),
(28, '9780134685999', 'Concrete Mathematics: A Foundation for Computer Science', 6, 6, 5, 1994, '2nd Edition', 'English', 'Summations, recurrence relations, generating functions, and discrete calculus by Knuth.'),
(29, '9780136006633', 'Cryptography and Network Security', 4, 3, 1, 2017, '7th Edition', 'English', 'Symmetric encryption, public key infrastructure, RSA, AES, SHA algorithms, and SSL/TLS.'),
(30, '9781492043454', 'Kubernetes: Up and Running', 4, 3, 4, 2019, '2nd Edition', 'English', 'Dive into container orchestration, pods, deployments, service discovery, and cloud native ops.'),
(31, '9780321751041', 'The Art of Scalability', 1, 5, 5, 2015, '2nd Edition', 'English', 'Scalable web architecture, organizational growth, risk management, and cloud architecture.'),
(32, '9780134444321', 'Compilers: Principles, Techniques, and Tools', 2, 1, 1, 2006, '2nd Edition', 'English', 'The legendary Dragon Book on lexical analysis, syntax parsing, semantic analysis, and code generation.');

-- ----------------------------------------------------------------------------
-- BOOK_COPIES (76 Physical Copies Across Shelves)
-- ----------------------------------------------------------------------------
INSERT INTO BOOK_COPIES (copy_id, book_id, accession_number, shelf_location, status) VALUES
-- Book 1: Clean Code (3 copies)
(1, 1, 'ACC-00101', 'Shelf CS-A1', 'AVAILABLE'),
(2, 1, 'ACC-00102', 'Shelf CS-A1', 'ISSUED'),
(3, 1, 'ACC-00103', 'Shelf CS-A1', 'AVAILABLE'),

-- Book 2: CLRS Algorithms (4 copies)
(4, 2, 'ACC-00201', 'Shelf CS-A2', 'AVAILABLE'),
(5, 2, 'ACC-00202', 'Shelf CS-A2', 'AVAILABLE'),
(6, 2, 'ACC-00203', 'Shelf CS-A2', 'ISSUED'),
(7, 2, 'ACC-00204', 'Shelf CS-A2', 'AVAILABLE'),

-- Book 3: Database System Concepts (3 copies)
(8, 3, 'ACC-00301', 'Shelf DB-B1', 'AVAILABLE'),
(9, 3, 'ACC-00302', 'Shelf DB-B1', 'ISSUED'),
(10, 3, 'ACC-00303', 'Shelf DB-B1', 'AVAILABLE'),

-- Book 4: Computer Networks (3 copies)
(11, 4, 'ACC-00401', 'Shelf NET-C1', 'AVAILABLE'),
(12, 4, 'ACC-00402', 'Shelf NET-C1', 'AVAILABLE'),
(13, 4, 'ACC-00403', 'Shelf NET-C1', 'ISSUED'),

-- Book 5: Effective Java (2 copies)
(14, 5, 'ACC-00501', 'Shelf CS-A3', 'AVAILABLE'),
(15, 5, 'ACC-00502', 'Shelf CS-A3', 'AVAILABLE'),

-- Book 6: TAOCP Vol 1 (2 copies)
(16, 6, 'ACC-00601', 'Shelf REF-01', 'AVAILABLE'),
(17, 6, 'ACC-00602', 'Shelf REF-01', 'AVAILABLE'),

-- Book 7: Clean Architecture (2 copies)
(18, 7, 'ACC-00701', 'Shelf SE-D1', 'AVAILABLE'),
(19, 7, 'ACC-00702', 'Shelf SE-D1', 'ISSUED'),

-- Book 8: Operating Systems (2 copies)
(20, 8, 'ACC-00801', 'Shelf OS-E1', 'AVAILABLE'),
(21, 8, 'ACC-00802', 'Shelf OS-E1', 'AVAILABLE'),

-- Book 9: OS Concepts Silberschatz (3 copies)
(22, 9, 'ACC-00901', 'Shelf OS-E2', 'AVAILABLE'),
(23, 9, 'ACC-00902', 'Shelf OS-E2', 'ISSUED'),
(24, 9, 'ACC-00903', 'Shelf OS-E2', 'AVAILABLE'),

-- Book 10: Modern Operating Systems (2 copies)
(25, 10, 'ACC-01001', 'Shelf OS-E3', 'AVAILABLE'),
(26, 10, 'ACC-01002', 'Shelf OS-E3', 'AVAILABLE'),

-- Book 11: The Clean Coder (2 copies)
(27, 11, 'ACC-01101', 'Shelf SE-D2', 'AVAILABLE'),
(28, 11, 'ACC-01102', 'Shelf SE-D2', 'AVAILABLE'),

-- Book 12: Designing Data-Intensive Applications (3 copies - ALL BORROWED to test reservation queue!)
(29, 12, 'ACC-01201', 'Shelf DB-B2', 'ISSUED'),
(30, 12, 'ACC-01202', 'Shelf DB-B2', 'ISSUED'),
(31, 12, 'ACC-01203', 'Shelf DB-B2', 'ISSUED'),

-- Book 13: Domain-Driven Design (2 copies)
(32, 13, 'ACC-01301', 'Shelf SE-D3', 'AVAILABLE'),
(33, 13, 'ACC-01302', 'Shelf SE-D3', 'AVAILABLE'),

-- Book 14: Design Patterns GOF (3 copies)
(34, 14, 'ACC-01401', 'Shelf SE-D4', 'AVAILABLE'),
(35, 14, 'ACC-01402', 'Shelf SE-D4', 'ISSUED'),
(36, 14, 'ACC-01403', 'Shelf SE-D4', 'AVAILABLE'),

-- Book 15: The C Programming Language (2 copies)
(37, 15, 'ACC-01501', 'Shelf CS-A4', 'AVAILABLE'),
(38, 15, 'ACC-01502', 'Shelf CS-A4', 'AVAILABLE'),

-- Book 16: Head First Design Patterns (2 copies)
(39, 16, 'ACC-01601', 'Shelf SE-D5', 'AVAILABLE'),
(40, 16, 'ACC-01602', 'Shelf SE-D5', 'AVAILABLE'),

-- Book 17: Building Microservices (2 copies)
(41, 17, 'ACC-01701', 'Shelf ARCH-01', 'AVAILABLE'),
(42, 17, 'ACC-01702', 'Shelf ARCH-01', 'AVAILABLE'),

-- Book 18: Computer Organization (2 copies)
(43, 18, 'ACC-01801', 'Shelf HW-01', 'AVAILABLE'),
(44, 18, 'ACC-01802', 'Shelf HW-01', 'AVAILABLE'),

-- Book 19: AI Modern Approach (3 copies)
(45, 19, 'ACC-01901', 'Shelf AI-F1', 'AVAILABLE'),
(46, 19, 'ACC-01902', 'Shelf AI-F1', 'ISSUED'),
(47, 19, 'ACC-01903', 'Shelf AI-F1', 'AVAILABLE'),

-- Book 20: Deep Learning Goodfellow (2 copies)
(48, 20, 'ACC-02001', 'Shelf AI-F2', 'AVAILABLE'),
(49, 20, 'ACC-02002', 'Shelf AI-F2', 'AVAILABLE'),

-- Book 21: Hands-On ML (3 copies)
(50, 21, 'ACC-02101', 'Shelf AI-F3', 'AVAILABLE'),
(51, 21, 'ACC-02102', 'Shelf AI-F3', 'AVAILABLE'),
(52, 21, 'ACC-02103', 'Shelf AI-F3', 'ISSUED'),

-- Book 22: Reinforcement Learning (2 copies)
(53, 22, 'ACC-02201', 'Shelf AI-F4', 'AVAILABLE'),
(54, 22, 'ACC-02202', 'Shelf AI-F4', 'AVAILABLE'),

-- Book 23: Learning Python (2 copies)
(55, 23, 'ACC-02301', 'Shelf CS-A5', 'AVAILABLE'),
(56, 23, 'ACC-02302', 'Shelf CS-A5', 'AVAILABLE'),

-- Book 24: Effective C++ (2 copies)
(57, 24, 'ACC-02401', 'Shelf CS-A6', 'AVAILABLE'),
(58, 24, 'ACC-02402', 'Shelf CS-A6', 'AVAILABLE'),

-- Book 25: Database Systems Complete Book (2 copies)
(59, 25, 'ACC-02501', 'Shelf DB-B3', 'AVAILABLE'),
(60, 25, 'ACC-02502', 'Shelf DB-B3', 'AVAILABLE'),

-- Book 26: High Performance MySQL (2 copies)
(61, 26, 'ACC-02601', 'Shelf DB-B4', 'AVAILABLE'),
(62, 26, 'ACC-02602', 'Shelf DB-B4', 'AVAILABLE'),

-- Book 27: Theory of Computation (2 copies)
(63, 27, 'ACC-02701', 'Shelf MATH-01', 'AVAILABLE'),
(64, 27, 'ACC-02702', 'Shelf MATH-01', 'AVAILABLE'),

-- Book 28: Concrete Mathematics (2 copies)
(65, 28, 'ACC-02801', 'Shelf MATH-02', 'AVAILABLE'),
(66, 28, 'ACC-02802', 'Shelf MATH-02', 'AVAILABLE'),

-- Book 29: Cryptography & Network Security (2 copies)
(67, 29, 'ACC-02901', 'Shelf NET-C2', 'AVAILABLE'),
(68, 29, 'ACC-02902', 'Shelf NET-C2', 'AVAILABLE'),

-- Book 30: Kubernetes Up & Running (2 copies)
(69, 30, 'ACC-03001', 'Shelf CLOUD-01', 'AVAILABLE'),
(70, 30, 'ACC-03002', 'Shelf CLOUD-01', 'AVAILABLE'),

-- Book 31: Art of Scalability (2 copies)
(71, 31, 'ACC-03101', 'Shelf ARCH-02', 'AVAILABLE'),
(72, 31, 'ACC-03102', 'Shelf ARCH-02', 'AVAILABLE'),

-- Book 32: Compilers Dragon Book (2 copies)
(73, 32, 'ACC-03201', 'Shelf CS-A7', 'AVAILABLE'),
(74, 32, 'ACC-03202', 'Shelf CS-A7', 'AVAILABLE');

-- ----------------------------------------------------------------------------
-- ISSUES (Historical, Current Active, and Overdue Loans)
-- ----------------------------------------------------------------------------
-- Historical (Returned without overdue fine)
INSERT INTO ISSUES (issue_id, student_id, copy_id, issue_date, due_date, return_date, status) VALUES
(1, 1, 1, DATEADD('DAY', -30, CURRENT_DATE), DATEADD('DAY', -16, CURRENT_DATE), DATEADD('DAY', -18, CURRENT_DATE), 'RETURNED'),
(2, 2, 4, DATEADD('DAY', -45, CURRENT_DATE), DATEADD('DAY', -31, CURRENT_DATE), DATEADD('DAY', -33, CURRENT_DATE), 'RETURNED'),
(3, 3, 8, DATEADD('DAY', -25, CURRENT_DATE), DATEADD('DAY', -11, CURRENT_DATE), DATEADD('DAY', -12, CURRENT_DATE), 'RETURNED');

-- Historical (Returned WITH Overdue Fine)
INSERT INTO ISSUES (issue_id, student_id, copy_id, issue_date, due_date, return_date, status) VALUES
(4, 1, 14, DATEADD('DAY', -20, CURRENT_DATE), DATEADD('DAY', -6, CURRENT_DATE), DATEADD('DAY', -2, CURRENT_DATE), 'RETURNED'),
(5, 4, 11, DATEADD('DAY', -40, CURRENT_DATE), DATEADD('DAY', -26, CURRENT_DATE), DATEADD('DAY', -20, CURRENT_DATE), 'RETURNED');

-- Current Active Loans (Within Due Date)
INSERT INTO ISSUES (issue_id, student_id, copy_id, issue_date, due_date, return_date, status) VALUES
(6, 1, 2, DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', 9, CURRENT_DATE), NULL, 'ISSUED'),
(7, 2, 6, DATEADD('DAY', -3, CURRENT_DATE), DATEADD('DAY', 11, CURRENT_DATE), NULL, 'ISSUED'),
(8, 3, 9, DATEADD('DAY', -7, CURRENT_DATE), DATEADD('DAY', 7, CURRENT_DATE), NULL, 'ISSUED'),
(9, 4, 13, DATEADD('DAY', -2, CURRENT_DATE), DATEADD('DAY', 12, CURRENT_DATE), NULL, 'ISSUED'),
(10, 5, 19, DATEADD('DAY', -4, CURRENT_DATE), DATEADD('DAY', 10, CURRENT_DATE), NULL, 'ISSUED'),
(11, 6, 35, DATEADD('DAY', -8, CURRENT_DATE), DATEADD('DAY', 6, CURRENT_DATE), NULL, 'ISSUED'),
(12, 7, 46, DATEADD('DAY', -1, CURRENT_DATE), DATEADD('DAY', 13, CURRENT_DATE), NULL, 'ISSUED');

-- Overdue Active Loans (Exceeded Due Date!)
INSERT INTO ISSUES (issue_id, student_id, copy_id, issue_date, due_date, return_date, status) VALUES
(13, 8, 23, DATEADD('DAY', -20, CURRENT_DATE), DATEADD('DAY', -6, CURRENT_DATE), NULL, 'OVERDUE'),
(14, 9, 29, DATEADD('DAY', -25, CURRENT_DATE), DATEADD('DAY', -11, CURRENT_DATE), NULL, 'OVERDUE'),
(15, 10, 30, DATEADD('DAY', -18, CURRENT_DATE), DATEADD('DAY', -4, CURRENT_DATE), NULL, 'OVERDUE'),
(16, 11, 31, DATEADD('DAY', -16, CURRENT_DATE), DATEADD('DAY', -2, CURRENT_DATE), NULL, 'OVERDUE'),
(17, 12, 52, DATEADD('DAY', -22, CURRENT_DATE), DATEADD('DAY', -8, CURRENT_DATE), NULL, 'OVERDUE');

-- ----------------------------------------------------------------------------
-- FINES (Paid and Unpaid Records)
-- ----------------------------------------------------------------------------
-- Issue 4 was 4 days late: 4 * 5 = INR 20 (Marked PAID)
INSERT INTO FINES (fine_id, issue_id, student_id, amount, payment_status, payment_date) VALUES
(1, 4, 1, 20.00, 'PAID', DATEADD('DAY', -1, CURRENT_DATE));

-- Issue 5 was 6 days late: 6 * 5 = INR 30 (UNPAID)
INSERT INTO FINES (fine_id, issue_id, student_id, amount, payment_status, payment_date) VALUES
(2, 5, 4, 30.00, 'UNPAID', NULL);

-- Additional existing fines for student 8 & student 11
INSERT INTO FINES (fine_id, issue_id, student_id, amount, payment_status, payment_date) VALUES
(3, 13, 8, 30.00, 'UNPAID', NULL),
(4, 16, 11, 10.00, 'UNPAID', NULL);

-- ----------------------------------------------------------------------------
-- RESERVATIONS (Queue for Book 12: Designing Data-Intensive Applications)
-- ----------------------------------------------------------------------------
INSERT INTO RESERVATIONS (reservation_id, student_id, book_id, reservation_date, status, queue_number) VALUES
(1, 1, 12, DATEADD('DAY', -2, CURRENT_DATE), 'PENDING', 1),
(2, 2, 12, DATEADD('DAY', -1, CURRENT_DATE), 'PENDING', 2),
(3, 5, 12, CURRENT_DATE, 'PENDING', 3);

-- ----------------------------------------------------------------------------
-- AUDIT_LOG (Realistic System Audit Trail)
-- ----------------------------------------------------------------------------
INSERT INTO AUDIT_LOG (log_id, user_id, action, action_date) VALUES
(1, 1, 'System initialized with master configuration and catalog', DATEADD('DAY', -60, CURRENT_DATE)),
(2, 2, 'Batch imported 32 titles and 74 physical accessions', DATEADD('DAY', -58, CURRENT_DATE)),
(3, 2, 'Approved library membership for 12 enrolled undergraduate students', DATEADD('DAY', -50, CURRENT_DATE)),
(4, 2, 'Issued Book ID 1 to Student 1 (Reg: 21CS001)', DATEADD('DAY', -30, CURRENT_DATE)),
(5, 3, 'Processed return for Book ID 1 from Student 1', DATEADD('DAY', -18, CURRENT_DATE)),
(6, 2, 'Issued Book ID 12 (Copy 29) to Student 9 (Reg: 23CS077)', DATEADD('DAY', -25, CURRENT_DATE)),
(7, 3, 'Placed reservation for Book ID 12 by Student 1 (Queue #1)', DATEADD('DAY', -2, CURRENT_DATE)),
(8, 2, 'Processed fine payment of INR 20.00 for Student 1 (Issue ID 4)', DATEADD('DAY', -1, CURRENT_DATE));
