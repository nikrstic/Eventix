CREATE TABLE seats (
                       id UUID PRIMARY KEY,
                       status VARCHAR(50) NOT NULL,
                       held_by UUID
);
INSERT INTO seats (id, status, held_by) VALUES ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'AVAILABLE', NULL);
INSERT INTO seats (id, status, held_by) VALUES ('b1ffcd88-8b0a-3de7-aa5c-5aa8ac270b22', 'AVAILABLE', NULL);