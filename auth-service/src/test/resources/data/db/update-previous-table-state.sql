DELETE FROM auth.user_credentials;
INSERT INTO auth.user_credentials(id, email, password, role, refresh_token)
VALUES
    ('1a72a05f-4b8f-43c5-a889-1ebc6d9dc729', 'test1@example.com', 'password1', 'USER', 'eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjFhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MTY4NiwiZXhwIjoxNzM3NTA4NjEyfQ.A95EowuYZwBQxiK93ROA2vH-FLCd5B1aD-v9Rkowv08'),
    ('2a72a05f-4b8f-43c5-a889-1ebc6d9dc729', 'test2@example.com', 'password2', 'USER', 'eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjJhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MDM1NSwiZXhwIjoxNzA2NTU1MTU1fQ.jLQeNvJwLe315bNZL8PoBPiYh3ZuOIRY9Rb4zznV0vE'),
    ('3a72a05f-4b8f-43c5-a889-1ebc6d9dc729', 'test3@example.com', 'password3', 'USER', 'eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjNhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MDM3NSwiZXhwIjoxNzA2NTU1MTc1fQ.IKoJSHaqHxWFucJ81fR10nD-ghD5Ki8gB2wLDxWXBYA'),
    ('4a72a05f-4b8f-43c5-a889-1ebc6d9dc729', 'test4@example.com', 'password4', 'USER', 'eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjRhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MDM5OCwiZXhwIjoxNzA2NTU1MTk4fQ.UCKOajwziK1zTmWCFumoC2pFQ0daCWmYXVCzXbUNaxw'),
    ('5a72a05f-4b8f-43c5-a889-1ebc6d9dc729', 'test5@example.com', 'password5', 'USER', 'eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjVhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk1MTA4MSwiZXhwIjoxNzA1OTUxMDg3fQ.yhbnBVDRq-3uJ6WSZ20L0iH_h-RWbNZZG90xvI9Umw0')