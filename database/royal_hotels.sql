-- ============================================================
--  ROYAL HOTELS - Database Schema
--  File   : database/royal_hotels.sql
--  Engine : InnoDB  |  MySQL 8.x compatible
-- ============================================================
--  SAFE TO RUN: Uses IF NOT EXISTS and INSERT IGNORE only.
--  Does NOT use DROP TABLE or DROP DATABASE.
-- ============================================================

-- Step 1: Create and open the database
USE defaultdb;

-- ============================================================
-- TABLE 1: customers
-- Stores personal details of every guest who makes a booking.
-- ============================================================
CREATE TABLE IF NOT EXISTS customers (
    customer_id  INT           NOT NULL AUTO_INCREMENT,
    name         VARCHAR(100)  NOT NULL,
    phone        VARCHAR(15)   NOT NULL,
    email        VARCHAR(150)  NOT NULL,
    address      VARCHAR(255)      NULL,
    id_proof     VARCHAR(100)      NULL,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_customers      PRIMARY KEY (customer_id),
    CONSTRAINT uq_customer_email UNIQUE (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Hotel guests and customers';

-- ============================================================
-- TABLE 2: rooms
-- Stores every physical room in the hotel.
-- status: AVAILABLE | BOOKED | OCCUPIED | MAINTENANCE
-- ============================================================
CREATE TABLE IF NOT EXISTS rooms (
    room_id         INT            NOT NULL AUTO_INCREMENT,
    room_number     VARCHAR(10)    NOT NULL,
    room_type       ENUM('Standard','Deluxe','Suite') NOT NULL,
    price_per_night DECIMAL(10,2)  NOT NULL,
    capacity        TINYINT        NOT NULL DEFAULT 2,
    status          ENUM('AVAILABLE','BOOKED','OCCUPIED','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    created_at      TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_rooms       PRIMARY KEY (room_id),
    CONSTRAINT uq_room_number UNIQUE (room_number)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Hotel room catalogue';

-- ============================================================
-- TABLE 3: bookings
-- Links a customer to a room for a date range.
-- booking_status: CONFIRMED | CHECKED_IN | CHECKED_OUT | CANCELLED
-- ============================================================
CREATE TABLE IF NOT EXISTS bookings (
    booking_id       INT            NOT NULL AUTO_INCREMENT,
    customer_id      INT            NOT NULL,
    room_id          INT            NOT NULL,
    check_in         DATE           NOT NULL,
    check_out        DATE           NOT NULL,
    number_of_guests TINYINT        NOT NULL DEFAULT 1,
    number_of_nights SMALLINT       NOT NULL DEFAULT 1,
    total_amount     DECIMAL(10,2)  NOT NULL,
    booking_status   ENUM('CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_bookings           PRIMARY KEY (booking_id),
    CONSTRAINT fk_booking_customer   FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_booking_room       FOREIGN KEY (room_id)     REFERENCES rooms(room_id)         ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_checkout_after_checkin CHECK (check_out > check_in),
    CONSTRAINT chk_number_of_guests       CHECK (number_of_guests >= 1),
    CONSTRAINT chk_number_of_nights       CHECK (number_of_nights >= 1),
    CONSTRAINT chk_total_amount           CHECK (total_amount >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Room booking records';

-- ============================================================
-- TABLE 4: payments
-- Records payment transactions linked to a booking.
-- payment_method : Cash | UPI | Card
-- payment_status : PAID | PENDING
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    payment_id     INT            NOT NULL AUTO_INCREMENT,
    booking_id     INT            NOT NULL,
    amount         DECIMAL(10,2)  NOT NULL,
    payment_method ENUM('Cash','UPI','Card') NOT NULL,
    payment_status ENUM('PAID','PENDING')    NOT NULL DEFAULT 'PENDING',
    payment_date   DATE           NOT NULL,
    CONSTRAINT pk_payments        PRIMARY KEY (payment_id),
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_payment_amount CHECK (amount >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Payment records for bookings';

-- ============================================================
-- TABLE 5: admins
-- Stores authorized administrators who can access the dashboard.
-- Passwords must be hashed using BCrypt (never plaintext).
-- ============================================================
CREATE TABLE IF NOT EXISTS admins (
    admin_id      INT AUTO_INCREMENT NOT NULL,
    username      VARCHAR(50)        NOT NULL,
    password_hash VARCHAR(255)       NOT NULL,
    full_name     VARCHAR(100)       NOT NULL,
    role          VARCHAR(20)        NOT NULL DEFAULT 'ROLE_ADMIN',
    created_at    TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_admins PRIMARY KEY (admin_id),
    CONSTRAINT uq_admin_username UNIQUE (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = 'Hotel administrative staff accounts';

-- ============================================================
-- SAMPLE ROOM DATA
-- Only rooms are seeded. No fake customers, bookings or payments.
-- INSERT IGNORE means running this script twice will not fail.
-- ============================================================

-- Standard Rooms: INR 3,500 per night, up to 2 guests
INSERT IGNORE INTO rooms (room_number, room_type, price_per_night, capacity, status) VALUES
    ('101', 'Standard', 3500.00, 2, 'AVAILABLE'),
    ('102', 'Standard', 3500.00, 2, 'AVAILABLE'),
    ('103', 'Standard', 3500.00, 2, 'AVAILABLE');

-- Deluxe Rooms: INR 6,500 per night, up to 3 guests
INSERT IGNORE INTO rooms (room_number, room_type, price_per_night, capacity, status) VALUES
    ('201', 'Deluxe', 6500.00, 2, 'AVAILABLE'),
    ('202', 'Deluxe', 6500.00, 2, 'AVAILABLE'),
    ('203', 'Deluxe', 6500.00, 3, 'AVAILABLE');

-- Suites: INR 12,000 per night, up to 4 guests
INSERT IGNORE INTO rooms (room_number, room_type, price_per_night, capacity, status) VALUES
    ('301', 'Suite', 12000.00, 4, 'AVAILABLE'),
    ('302', 'Suite', 12000.00, 4, 'AVAILABLE');

-- ============================================================
-- VERIFICATION QUERIES (paste into a new query tab to verify)
-- ============================================================
-- SHOW TABLES;
-- DESCRIBE customers;
-- DESCRIBE rooms;
-- DESCRIBE bookings;
-- DESCRIBE payments;
-- SELECT room_number, room_type, price_per_night, capacity, status FROM rooms;