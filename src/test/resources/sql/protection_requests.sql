INSERT INTO protection_request (id, requester_address, email, first_name, last_name, phone_number, city, country, line1, postal_code)
VALUES ('d9aea58aa7d24a768b74aff7b82380e1', '5Ew3MyB15VprZrjQVkpQFj8okmc9xLDSEdNhqMMS5cXsqxoW', 'john.doe@logion.network', 'John', 'Doe', '+1234', 'Liège', 'Belgium', 'Place de le République Française', '4000');

INSERT INTO legal_officer_decision(legal_officer_address, status, request_id)
VALUES ('5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY', 'PENDING', 'd9aea58aa7d24a768b74aff7b82380e1'); -- ALICE

INSERT INTO legal_officer_decision(legal_officer_address, status, request_id)
VALUES ('5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty', 'PENDING', 'd9aea58aa7d24a768b74aff7b82380e1'); -- BOB