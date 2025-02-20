ALTER DATABASE spotdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE spotdb;

CREATE TABLE job (
                     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                     content VARCHAR(255),
                     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                     deleted_at TIMESTAMP NULL DEFAULT NULL,
                     img VARCHAR(1000),
                     lat FLOAT(53),
                     lng FLOAT(53),
                     money INTEGER,
                     started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE members (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         account VARCHAR(255),
                         deleted_at TIMESTAMP NULL DEFAULT NULL,
                         email VARCHAR(255),
                         lat FLOAT(53) NOT NULL,
                         lng FLOAT(53) NOT NULL,
                         nickname VARCHAR(255),
                         point INTEGER NOT NULL
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE ability (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         title VARCHAR(255)
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE matching (
                          id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          is_done BIT NOT NULL,
                          role INTEGER NOT NULL,
                          job_id BIGINT NOT NULL,
                          member_id BIGINT NOT NULL,
                          FOREIGN KEY (job_id) REFERENCES job (id) ON DELETE CASCADE,
                          FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE certification (
                               id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                               matching_id BIGINT NOT NULL,
                               FOREIGN KEY (matching_id) REFERENCES matching (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE chat_rooms (
                            id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            thumbnail_image_url VARCHAR(255),
                            title VARCHAR(255),
                            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            job_id BIGINT NOT NULL,
                            FOREIGN KEY (job_id) REFERENCES job (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE chat_participants (
                                   id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   chat_room_id BIGINT NOT NULL,
                                   member_id BIGINT NOT NULL,
                                   FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
                                   FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE messages (
                          id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          content VARCHAR(255),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          chat_room_id BIGINT NOT NULL,
                          sender_id BIGINT NOT NULL,
                          FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
                          FOREIGN KEY (sender_id) REFERENCES members (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE message_status (
                                id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                is_read BIT NOT NULL,
                                message_id BIGINT NOT NULL,
                                chat_room_id BIGINT NOT NULL,
                                member_id BIGINT NOT NULL,
                                FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
                                FOREIGN KEY (chat_room_id) REFERENCES chat_rooms (id) ON DELETE CASCADE,
                                FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE review (
                        id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                        content VARCHAR(300),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        deleted_at TIMESTAMP NULL DEFAULT NULL,  -- ✅ `,` 추가됨
                        job_id BIGINT NOT NULL,
                        rate INTEGER NOT NULL,
                        target_id BIGINT NOT NULL,
                        writer_id BIGINT NOT NULL,
                        FOREIGN KEY (job_id) REFERENCES job (id) ON DELETE CASCADE,
                        FOREIGN KEY (target_id) REFERENCES members (id) ON DELETE CASCADE,
                        FOREIGN KEY (writer_id) REFERENCES members (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE worker (
                        member_id BIGINT NOT NULL PRIMARY KEY,
                        introduction VARCHAR(255),
                        is_working TINYINT(1),
                        registered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (member_id) REFERENCES members (id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE TABLE worker_ability (
                                id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                ability_id BIGINT NOT NULL,
                                member_id BIGINT NOT NULL,
                                FOREIGN KEY (ability_id) REFERENCES ability (id) ON DELETE CASCADE,
                                FOREIGN KEY (member_id) REFERENCES worker (member_id) ON DELETE CASCADE
) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;